<template>
  <div class="app-container analysis-page">
    <section class="analysis-card filter-card">
      <div class="section-title">{{ pageTitle }}</div>
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="经营日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="yyyy-MM-dd"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="平台"><el-input v-model="query.platform" clearable placeholder="全部平台" /></el-form-item>
        <el-form-item label="店铺"><el-input v-model="query.shopName" clearable placeholder="全部店铺" /></el-form-item>
        <el-form-item label="品牌"><el-input v-model="query.brand" clearable placeholder="全部品牌" /></el-form-item>
        <el-form-item label="货品编码"><el-input v-model="query.goodsNo" clearable placeholder="全部货品" /></el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button v-if="showTaskActions" icon="el-icon-download" @click="runSync">同步前一日</el-button>
          <el-button v-if="showTaskActions" icon="el-icon-refresh-right" @click="runRebuild">重算前一日</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="metric-grid">
      <article v-for="item in cards" :key="item.key" class="analysis-card metric-card">
        <span>{{ item.label }}</span>
        <strong :class="{ pending: item.pending }">{{ item.value }}</strong>
        <small v-if="item.tip">{{ item.tip }}</small>
      </article>
    </section>

    <section v-if="isQuality" class="analysis-card quality-summary">
      <el-progress :percentage="completenessRate" :status="incompleteCount ? 'warning' : 'success'" />
      <div>事实记录 {{ factCount }} 条，缺失核算数据 {{ incompleteCount }} 条</div>
    </section>

    <section class="analysis-card table-card">
      <div class="table-toolbar">
        <div class="section-title">{{ tableTitle }}</div>
        <span>共 {{ visibleRows.length }} 条</span>
      </div>
      <el-table v-loading="loading" :data="pagedRows" border stripe>
        <el-table-column prop="metricDate" label="日期" width="110" fixed="left" />
        <el-table-column prop="subjectName" label="主体" min-width="130" show-overflow-tooltip />
        <el-table-column prop="platform" label="平台" width="100" />
        <el-table-column prop="shopName" label="店铺" min-width="150" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" width="110" />
        <el-table-column prop="category" label="品类" width="110" />
        <el-table-column prop="goodsNo" label="货品编码" min-width="130" show-overflow-tooltip />
        <el-table-column prop="salesQuantity" label="销量" width="90" align="right" />
        <el-table-column v-if="apiType === 'humanEfficiency'" prop="directHeadcount" label="直接人数" width="100" align="right" />
        <el-table-column v-if="apiType === 'humanEfficiency'" prop="indirectHeadcount" label="间接人数" width="100" align="right" />
        <el-table-column label="销售收入" width="120" align="right">
          <template slot-scope="scope">{{ money(scope.row.salesRevenue) }}</template>
        </el-table-column>
        <el-table-column label="商品成本" width="120" align="right">
          <template slot-scope="scope">{{ money(scope.row.goodsCost) }}</template>
        </el-table-column>
        <el-table-column label="商品毛利" width="120" align="right">
          <template slot-scope="scope">{{ money(scope.row.goodsGrossProfit) }}</template>
        </el-table-column>
        <el-table-column label="履约毛利" width="120" align="right">
          <template slot-scope="scope">{{ money(scope.row.fulfillmentGrossProfit) }}</template>
        </el-table-column>
        <el-table-column label="经营利润" width="120" align="right">
          <template slot-scope="scope">{{ money(scope.row.operatingProfit) }}</template>
        </el-table-column>
        <el-table-column prop="incompleteCount" label="缺失数" width="90" align="right" />
        <el-table-column v-if="isQuality" prop="missingReason" label="待核算原因" min-width="180" show-overflow-tooltip />
      </el-table>
      <el-pagination
        v-if="visibleRows.length > pageSize"
        :current-page.sync="pageNum"
        :page-size="pageSize"
        :total="visibleRows.length"
        layout="prev, pager, next, total"
      />
    </section>

    <section v-if="metricTree.length" class="analysis-card metric-tree">
      <div class="section-title">经营指标树</div>
      <div class="tree-grid">
        <div v-for="node in metricTree" :key="node.key" class="tree-node">
          <span>{{ node.name }}</span>
          <strong>{{ money(node.value) }}</strong>
          <el-tag :type="node.calcStatus === 'COMPLETE' ? 'success' : 'warning'" size="mini">
            {{ node.calcStatus === 'COMPLETE' ? '已核算' : '待核算' }}
          </el-tag>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { getAnalysisDashboard, rebuildAnalysis, runAnalysisSync } from '@/api/analysis'

export default {
  name: 'AnalysisDashboard',
  data() {
    return {
      loading: false,
      dateRange: [],
      query: { platform: '', shopName: '', brand: '', goodsNo: '' },
      summary: {},
      rows: [],
      metricTree: [],
      factCount: 0,
      incompleteCount: 0,
      completenessRate: 100,
      pageNum: 1,
      pageSize: 20
    }
  },
  computed: {
    apiType() {
      const path = this.$route.path
      const mapping = {
        performanceRollup: 'performanceRollup',
        channelProduction: 'channelProduction',
        humanEfficiency: 'humanEfficiency',
        metricTree: 'metricTree',
        orderDetails: 'orderDetails',
        dataQuality: 'dataQuality'
      }
      const key = Object.keys(mapping).find(item => path.indexOf(item) !== -1)
      return key ? mapping[key] : 'operationStats'
    },
    pageTitle() { return this.$route.meta.title || '经营统计' },
    tableTitle() { return this.isQuality ? '数据质量异常明细' : `${this.pageTitle}明细` },
    showTaskActions() { return this.apiType === 'operationStats' },
    isQuality() { return this.apiType === 'dataQuality' },
    visibleRows() {
      if (this.isQuality) return this.rows.filter(item => Number(item.incompleteCount || 0) > 0)
      return this.rows
    },
    pagedRows() {
      const start = (this.pageNum - 1) * this.pageSize
      return this.visibleRows.slice(start, start + this.pageSize)
    },
    cards() {
      const data = this.summary || {}
      return [
        { key: 'quantity', label: '销售数量', value: this.number(data.salesQuantity) },
        { key: 'revenue', label: '销售收入', value: this.money(data.salesRevenue) },
        { key: 'cost', label: '商品成本', value: this.money(data.goodsCost), pending: data.goodsCost == null },
        { key: 'gross', label: '商品毛利', value: this.money(data.goodsGrossProfit), pending: data.goodsGrossProfit == null },
        { key: 'fulfillment', label: '履约毛利', value: this.money(data.fulfillmentGrossProfit), pending: data.fulfillmentGrossProfit == null },
        { key: 'profit', label: '经营利润', value: this.money(data.operatingProfit), pending: data.operatingProfit == null },
        { key: 'margin', label: '商品毛利率', value: this.percent(data.goodsGrossMargin), pending: data.goodsGrossMargin == null },
        { key: 'quality', label: '数据完整率', value: `${this.completenessRate || 0}%`, tip: `缺失 ${this.incompleteCount} 条` }
      ]
    }
  },
  watch: {
    '$route.fullPath'() { this.loadData() }
  },
  created() {
    const yesterday = new Date(Date.now() - 86400000)
    const end = this.formatDate(yesterday)
    const start = this.formatDate(new Date(yesterday.getTime() - 29 * 86400000))
    this.dateRange = [start, end]
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = Object.assign({}, this.query, {
          startDate: this.dateRange && this.dateRange[0],
          endDate: this.dateRange && this.dateRange[1]
        })
        const response = await getAnalysisDashboard(this.apiType, params)
        const data = response.data || {}
        if (Array.isArray(data)) {
          if (this.apiType === 'metricTree') {
            this.metricTree = data
            this.rows = []
          } else {
            this.rows = data.map(this.normalizeFactRow)
            this.metricTree = []
          }
          this.summary = {}
          this.factCount = data.length
          this.incompleteCount = this.rows.filter(item => item.incompleteCount > 0).length
          this.completenessRate = this.factCount ? Number(((this.factCount - this.incompleteCount) * 100 / this.factCount).toFixed(2)) : 100
          this.pageNum = 1
          return
        }
        this.summary = data.summary || {}
        this.rows = data.rows || []
        this.metricTree = data.metricTree || []
        this.factCount = Number(data.factCount || 0)
        this.incompleteCount = Number(data.incompleteCount || 0)
        this.completenessRate = Number(data.completenessRate == null ? 100 : data.completenessRate)
        this.pageNum = 1
      } finally {
        this.loading = false
      }
    },
    resetQuery() {
      this.query = { platform: '', shopName: '', brand: '', goodsNo: '' }
      this.loadData()
    },
    async runSync() {
      const date = this.formatDate(new Date(Date.now() - 86400000))
      await this.$confirm(`确认同步 ${date} 的吉客云经营数据吗？`, '同步确认')
      await runAnalysisSync(date)
      this.$message.success('同步完成')
      this.loadData()
    },
    async runRebuild() {
      const date = this.formatDate(new Date(Date.now() - 86400000))
      await rebuildAnalysis(date)
      this.$message.success('重算完成')
      this.loadData()
    },
    money(value) {
      return value == null ? '待核算' : `¥${Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
    },
    number(value) { return Number(value || 0).toLocaleString('zh-CN') },
    percent(value) { return value == null ? '待核算' : `${Number(value).toFixed(2)}%` },
    normalizeFactRow(item) {
      const subsidy = Number(item.platformSubsidy || 0) + Number(item.governmentSubsidy || 0)
      return Object.assign({}, item, {
        metricDate: item.businessDate,
        salesQuantity: item.quantity,
        salesRevenue: Number(item.paymentAmount || 0) + subsidy,
        incompleteCount: item.calcStatus === 'COMPLETE' ? 0 : 1
      })
    },
    formatDate(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style lang="scss" scoped>
.analysis-page { color: var(--nl-color); background: var(--bg-page); min-height: 100%; }
.analysis-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); box-shadow: var(--shadow-card); }
.filter-card, .table-card, .metric-tree, .quality-summary { padding: var(--page-card-padding); margin-bottom: var(--page-section-gap); }
.section-title { color: var(--nl-color-title); font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.metric-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: var(--page-section-gap); margin-bottom: var(--page-section-gap); }
.metric-card { padding: 18px; display: flex; flex-direction: column; gap: 8px; }
.metric-card span, .metric-card small { color: var(--nl-color-weak); }
.metric-card strong { color: var(--nl-color-title); font-size: 23px; }
.metric-card strong.pending { color: #e6a23c; font-size: 18px; }
.table-toolbar { display: flex; align-items: center; justify-content: space-between; color: var(--nl-color-tip); }
.table-toolbar .section-title { margin-bottom: 12px; }
.el-pagination { margin-top: 16px; text-align: right; }
.quality-summary { display: grid; grid-template-columns: minmax(240px, 420px) 1fr; align-items: center; gap: 24px; color: var(--nl-color-weak); }
.tree-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: 10px; }
.tree-node { background: var(--bg-hover); border-radius: var(--small-radius); padding: 14px; display: grid; gap: 8px; }
.tree-node span { color: var(--nl-color-weak); }
.tree-node strong { color: var(--nl-color-title); font-size: 18px; }
@media (max-width: 768px) { .quality-summary { grid-template-columns: 1fr; } }
</style>
