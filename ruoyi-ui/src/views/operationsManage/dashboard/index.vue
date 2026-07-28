<template>
  <div class="app-container analysis-page" :class="{ 'performance-rollup-page': isPerformanceRollup }">
    <operation-stats v-if="isOperationStats" />

    <section v-else-if="isPerformanceRollup" class="rollup-filter-panel">
      <div class="rollup-filter-toolbar">
        <div class="rollup-group-tabs" aria-label="绩效汇总维度">
          <button v-for="item in rollupGroupDimensions" :key="item.key" :class="{ active: rollupGroupDimension === item.key }" @click="selectRollupGroupDimension(item.key)">{{ item.label }}</button>
        </div>
        <el-select v-model="query.subjectName" clearable filterable placeholder="主体选择" @change="loadData">
          <el-option v-for="item in rollupDimensionOptions.subjectName" :key="item" :label="item" :value="item" />
        </el-select>
        <el-select v-model="query.orderType" clearable placeholder="全部类型订单" @change="loadData">
          <el-option v-for="item in rollupDimensionOptions.orderType" :key="item" :label="item" :value="item" />
        </el-select>
        <div class="rollup-toolbar-spacer" />
        <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" @change="loadData" />
        <div class="rollup-quick-range-group"><button v-for="day in [7, 15, 30]" :key="day" class="rollup-quick-range" :class="{ active: quickDays === day }" @click="setRollupQuickRange(day)">{{ day }}天</button></div>
      </div>
      <div v-if="rollupActiveFilters.length" class="rollup-active-filter-bar"><span>当前查询项</span><el-tag v-for="item in rollupActiveFilters" :key="item.key" closable size="small" @close="clearRollupFilter(item.key)">{{ item.label }}：{{ item.value }}</el-tag></div>
      <div v-show="!filterCollapsed" class="rollup-dimension-filter-list">
        <div v-for="dimension in rollupDimensions" :key="dimension.key" class="rollup-dimension-filter-row" :class="{ expanded: expandedDimensions[dimension.key] || dimension.key === 'brand' }">
          <div class="rollup-dimension-label"><i :class="dimension.icon" />{{ dimension.label }}</div>
          <button class="rollup-all-option" :class="{ selected: !query[dimension.key] }" @click="selectRollupDimension(dimension.key, '')">全选</button>
          <el-input v-if="dimension.searchable" v-model="dimensionKeyword[dimension.key]" class="rollup-dimension-search" size="mini" placeholder="搜索/筛选" clearable />
          <div class="rollup-dimension-options"><button v-for="item in visibleRollupDimensionOptions(dimension.key)" :key="item" class="rollup-dimension-option" :class="{ selected: query[dimension.key] === item }" @click="selectRollupDimension(dimension.key, item)">{{ item }}</button></div>
          <button v-if="dimension.key !== 'brand' && filteredRollupDimensionOptions(dimension.key).length > optionLimit" class="rollup-more-option" @click="toggleRollupMore(dimension.key)">{{ expandedDimensions[dimension.key] ? '收起' : '更多' }} <i :class="expandedDimensions[dimension.key] ? 'el-icon-caret-top' : 'el-icon-caret-bottom'" /></button>
        </div>
      </div>
      <button class="rollup-collapse-button" @click="toggleRollupFilter">{{ filterCollapsed ? '展开筛选' : '收起' }} <i :class="filterCollapsed ? 'el-icon-caret-bottom' : 'el-icon-caret-top'" /></button>
    </section>

    <section v-if="!isOperationStats && !isPerformanceRollup" class="analysis-card filter-card">
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

    <section v-if="!isOperationStats && !isPerformanceRollup" class="metric-grid">
      <article v-for="item in cards" :key="item.key" class="analysis-card metric-card">
        <span>{{ item.label }}</span>
        <strong :class="{ pending: item.pending }">{{ item.value }}</strong>
        <small v-if="item.tip">{{ item.tip }}</small>
      </article>
    </section>

    <section v-if="!isOperationStats && isQuality" class="analysis-card quality-summary">
      <el-progress :percentage="completenessRate" :status="incompleteCount ? 'warning' : 'success'" />
      <div>事实记录 {{ factCount }} 条，缺失核算数据 {{ incompleteCount }} 条</div>
    </section>

    <section v-else-if="isPerformanceRollup" ref="performanceRollupSection" class="analysis-card performance-rollup-section">
      <div ref="performanceRollupHeader" class="performance-rollup-header"><h2>绩效汇总</h2><el-button size="small" type="primary" plain icon="el-icon-download" @click="notifyPerformanceExport">导出汇总</el-button></div>
      <el-table ref="performanceRollupTable" v-loading="loading" :data="pagedRows" border stripe show-summary :height="performanceTableHeight" :summary-method="getPerformanceSummaries" class="performance-rollup-table">
        <el-table-column label="序号" width="96" align="center"><template slot-scope="scope">{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</template></el-table-column>
        <el-table-column prop="subjectName" label="所属主体" min-width="210" show-overflow-tooltip />
        <el-table-column prop="salesQuantity" label="销量" width="120" align="right" sortable><template slot-scope="scope">{{ number(scope.row.salesQuantity) }}</template></el-table-column>
        <el-table-column prop="salesRevenue" label="销售收入" min-width="170" align="right" sortable><template slot-scope="scope">{{ money(scope.row.salesRevenue) }}</template></el-table-column>
        <el-table-column prop="goodsGrossProfit" label="商品毛利" min-width="170" align="right" sortable><template slot-scope="scope">{{ money(scope.row.goodsGrossProfit) }}</template></el-table-column>
        <el-table-column prop="fulfillmentGrossProfit" label="履约毛利" min-width="170" align="right" sortable><template slot-scope="scope">{{ money(scope.row.fulfillmentGrossProfit) }}</template></el-table-column>
        <el-table-column prop="departmentProfit" label="部门利润" min-width="170" align="right" sortable><template slot-scope="scope">{{ money(scope.row.departmentProfit) }}</template></el-table-column>
        <el-table-column prop="operatingProfit" label="经营利润" min-width="170" align="right" sortable><template slot-scope="scope">{{ money(scope.row.operatingProfit) }}</template></el-table-column>
        <el-table-column label="订单详情/调整列" width="150" align="center"><template><span class="performance-detail-link">详情</span></template></el-table-column>
      </el-table>
      <el-pagination v-if="visibleRows.length > pageSize" ref="performancePagination" :current-page.sync="pageNum" :page-size="pageSize" :total="visibleRows.length" layout="prev, pager, next, total" />
    </section>

    <section v-else-if="!isOperationStats" class="analysis-card table-card">
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

  </div>
</template>

<script>
import { getAnalysisDashboard, getAnalysisDashboardFilterOptions, rebuildAnalysis, runAnalysisSync } from '@/api/analysis'
import OperationStats from './components/OperationStats.vue'

export default {
  name: 'AnalysisDashboard',
  components: { OperationStats },
  data() {
    return {
      loading: false,
      dateRange: [],
      query: { subjectName: '', orderType: '', category: '', brand: '', platform: '', shopName: '', goodsNo: '' },
      quickDays: 30,
      filterCollapsed: false,
      optionLimit: 10,
      expandedDimensions: {},
      dimensionKeyword: { shopName: '' },
      filterOptions: { platforms: [], stores: [], brands: [], categories: [] },
      rollupGroupDimension: 'subject',
      summary: {},
      rows: [],
      metricTree: [],
      factCount: 0,
      incompleteCount: 0,
      completenessRate: 100,
      pageNum: 1,
      pageSize: 20,
      performanceTableHeight: 320,
      performanceResizeHandler: null
    }
  },
  computed: {
    apiType() {
      const path = this.$route.path
      const mapping = {
        performanceRollup: 'performanceRollup',
        dataQuality: 'dataQuality'
      }
      const key = Object.keys(mapping).find(item => path.indexOf(item) !== -1)
      return key ? mapping[key] : 'operationStats'
    },
    pageTitle() { return this.$route.meta.title || '经营统计' },
    isOperationStats() { return this.apiType === 'operationStats' },
    isPerformanceRollup() { return this.apiType === 'performanceRollup' },
    rollupDimensions() { return [{ key: 'category', label: '品类', icon: 'el-icon-caret-top', searchable: false }, { key: 'brand', label: '品牌', icon: 'el-icon-caret-bottom', searchable: false }, { key: 'platform', label: '平台', icon: 'el-icon-caret-bottom', searchable: false }, { key: 'shopName', label: '店铺', icon: 'el-icon-caret-bottom', searchable: true }] },
    rollupGroupDimensions() { return [{ key: 'subject', label: '主体' }, { key: 'platform', label: '平台' }, { key: 'shop', label: '店铺' }, { key: 'brand', label: '品牌' }, { key: 'category', label: '类目' }, { key: 'sku', label: 'SKU' }] },
    rollupDimensionOptions() { return { subjectName: this.distinctRowOptions('subjectName'), orderType: this.distinctRowOptions('orderType'), category: this.filterOptions.categories, brand: this.filterOptions.brands, platform: this.filterOptions.platforms, shopName: this.rollupStoreOptions } },
    rollupStoreOptions() { return this.filterOptions.stores.filter(item => !this.query.platform || item.platformName === this.query.platform).map(item => item.shopName) },
    rollupActiveFilters() { return [{ key: 'subjectName', label: '主体', value: this.query.subjectName }, { key: 'orderType', label: '订单类型', value: this.query.orderType }, { key: 'category', label: '品类', value: this.query.category }, { key: 'brand', label: '品牌', value: this.query.brand }, { key: 'platform', label: '平台', value: this.query.platform }, { key: 'shopName', label: '店铺', value: this.query.shopName }].filter(item => item.value) },
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
    async '$route.fullPath'() {
      if (!this.isOperationStats) {
        if (this.isPerformanceRollup) await this.loadFilterOptions()
        this.loadData()
      }
    }
  },
  async created() {
    if (this.isOperationStats) return
    const yesterday = new Date(Date.now() - 86400000)
    const end = this.formatDate(yesterday)
    const start = this.formatDate(new Date(yesterday.getTime() - 29 * 86400000))
    this.dateRange = [start, end]
    if (this.isPerformanceRollup) await this.loadFilterOptions()
    this.loadData()
  },
  mounted() {
    this.performanceResizeHandler = this.updatePerformanceTableHeight
    window.addEventListener('resize', this.performanceResizeHandler)
    this.updatePerformanceTableHeight()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.performanceResizeHandler)
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
          this.rows = data.map(this.normalizeFactRow)
          this.metricTree = []
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
        this.updatePerformanceTableHeight()
      }
    },
    resetQuery() {
      this.query = { subjectName: '', orderType: '', category: '', brand: '', platform: '', shopName: '', goodsNo: '' }
      this.loadData()
    },
    async loadFilterOptions() {
      const response = await getAnalysisDashboardFilterOptions()
      this.filterOptions = response.data || { platforms: [], stores: [], brands: [], categories: [] }
    },
    distinctRowOptions(key) { return [...new Set(this.rows.map(row => row[key]).filter(Boolean))] },
    setRollupQuickRange(days) {
      this.quickDays = days
      const end = new Date()
      this.dateRange = [this.formatDate(this.addDays(end, -(days - 1))), this.formatDate(end)]
      this.loadData()
    },
    selectRollupDimension(key, value) {
      if (key === 'platform' && this.query.platform !== value) this.query.shopName = ''
      this.query[key] = value
      this.quickDays = 0
      this.loadData()
    },
    clearRollupFilter(key) {
      if (key === 'platform') this.query.shopName = ''
      this.query[key] = ''
      this.quickDays = 0
      this.loadData()
    },
    filteredRollupDimensionOptions(key) {
      const keyword = (this.dimensionKeyword[key] || '').trim().toLowerCase()
      const options = this.rollupDimensionOptions[key] || []
      return keyword ? options.filter(item => item.toLowerCase().includes(keyword)) : options
    },
    visibleRollupDimensionOptions(key) {
      const options = this.filteredRollupDimensionOptions(key)
      return key === 'brand' || this.expandedDimensions[key] ? options : options.slice(0, this.optionLimit)
    },
    toggleRollupMore(key) {
      this.$set(this.expandedDimensions, key, !this.expandedDimensions[key])
      this.updatePerformanceTableHeight()
    },
    toggleRollupFilter() {
      this.filterCollapsed = !this.filterCollapsed
      this.updatePerformanceTableHeight()
    },
    selectRollupGroupDimension(key) { this.rollupGroupDimension = key },
    updatePerformanceTableHeight() {
      if (!this.isPerformanceRollup) return
      this.$nextTick(() => {
        const section = this.$refs.performanceRollupSection
        const appMain = this.$el.closest('.app-main')
        const pagination = this.$refs.performancePagination
        if (!section || !appMain) return
        const table = this.$refs.performanceRollupTable
        if (!table || !table.$el) return
        const sectionStyle = window.getComputedStyle(section)
        const pageStyle = window.getComputedStyle(this.$el)
        const appMainStyle = window.getComputedStyle(appMain)
        const bottomPadding = Number.parseFloat(sectionStyle.paddingBottom) || 0
        const sectionBottomBorder = Number.parseFloat(sectionStyle.borderBottomWidth) || 0
        const pageBottomPadding = Number.parseFloat(pageStyle.paddingBottom) || 0
        const appMainBottomPadding = Number.parseFloat(appMainStyle.paddingBottom) || 0
        const paginationHeight = pagination ? pagination.$el.offsetHeight + 16 : 0
        const contentBottom = appMain.getBoundingClientRect().bottom - appMainBottomPadding - pageBottomPadding
        const tableTop = table.$el.getBoundingClientRect().top
        const availableHeight = contentBottom - tableTop - paginationHeight - bottomPadding - sectionBottomBorder
        this.performanceTableHeight = Math.max(80, Math.floor(availableHeight) - 4)
        this.$nextTick(() => this.$refs.performanceRollupTable && this.$refs.performanceRollupTable.doLayout())
      })
    },
    getPerformanceSummaries({ columns }) {
      return columns.map((column, index) => {
        if (index === 0) return ''
        if (index === 1) return '合计'
        if (column.property === 'salesQuantity') return this.number(this.summary.salesQuantity)
        if (column.property) return this.money(this.summary[column.property])
        return '--'
      })
    },
    notifyPerformanceExport() { this.$message.info('导出功能接入后可下载绩效汇总') },
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
    },
    addDays(date, days) {
      const copy = new Date(date)
      copy.setDate(copy.getDate() + days)
      return copy
    }
  }
}
</script>

<style lang="scss" scoped>
.analysis-page { color: var(--nl-color); background: var(--bg-page); min-height: 100%; }
.analysis-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); box-shadow: var(--shadow-card); }
.filter-card, .table-card, .quality-summary { padding: var(--page-card-padding); margin-bottom: var(--page-section-gap); }
.section-title { color: var(--nl-color-title); font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.metric-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(170px, 1fr)); gap: var(--page-section-gap); margin-bottom: var(--page-section-gap); }
.metric-card { padding: 18px; display: flex; flex-direction: column; gap: 8px; }
.metric-card span, .metric-card small { color: var(--nl-color-weak); }
.metric-card strong { color: var(--nl-color-title); font-size: 23px; }
.metric-card strong.pending { color: var(--nl-color-tip); font-size: 18px; }
.table-toolbar { display: flex; align-items: center; justify-content: space-between; color: var(--nl-color-tip); }
.table-toolbar .section-title { margin-bottom: 12px; }
.el-pagination { margin-top: 16px; text-align: right; }
.quality-summary { display: grid; grid-template-columns: minmax(240px, 420px) 1fr; align-items: center; gap: 24px; color: var(--nl-color-weak); }
.tree-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(190px, 1fr)); gap: 10px; }
.tree-node { background: var(--bg-hover); border-radius: var(--small-radius); padding: 14px; display: grid; gap: 8px; }
.tree-node span { color: var(--nl-color-weak); }
.tree-node strong { color: var(--nl-color-title); font-size: 18px; }
.rollup-filter-panel { padding: 18px 22px 16px; border: 1px solid var(--border-tags); border-radius: var(--large-radius); background: var(--bg-card); box-shadow: var(--shadow-card); }
.rollup-filter-toolbar { display: flex; align-items: center; gap: 16px; min-height: 56px; }
.rollup-group-tabs { display: flex; flex: 0 0 auto; overflow: hidden; border: 1px solid var(--border-tags); border-radius: var(--small-radius); }
.rollup-group-tabs button { display: grid; min-width: 74px; height: 56px; padding: 0 16px; place-items: center; border: 0; border-right: 1px solid var(--border-tags); background: var(--bg-hover); color: var(--nl-color); font-size: 18px; white-space: nowrap; cursor: pointer; }
.rollup-group-tabs button:last-child { border-right: 0; }
.rollup-group-tabs .active { background: var(--primary-color); color: var(--module-nav-active-text); }
.rollup-filter-toolbar :deep(.el-select) { width: 280px; }
.rollup-filter-toolbar :deep(.el-date-editor) { width: 480px; }
.rollup-filter-toolbar :deep(.el-input__inner) { height: 56px; border-color: var(--border-tags); background: var(--bg-hover); color: var(--nl-color); font-size: 18px; }
.rollup-toolbar-spacer { flex: 1; min-width: 32px; }
.rollup-quick-range-group { display: flex; align-self: stretch; overflow: hidden; border: 1px solid var(--border-tags); border-radius: var(--small-radius); }
.rollup-quick-range { min-width: 78px; padding: 0 16px; border: 0; border-right: 1px solid var(--border-tags); background: var(--bg-hover); color: var(--nl-color); font-size: 17px; font-weight: 600; cursor: pointer; }
.rollup-quick-range:last-child { border-right: 0; }
.rollup-quick-range.active { background: var(--primary-color); color: var(--module-nav-active-text); }
.rollup-active-filter-bar { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; margin-top: 12px; padding: 10px 12px; border-radius: var(--small-radius); background: var(--bg-hover); color: var(--nl-color-weak); font-size: 14px; }
.rollup-active-filter-bar :deep(.el-tag) { border-color: var(--primary-border); background: var(--primary-light-bg); color: var(--primary-color); }
.rollup-dimension-filter-list { margin-top: 16px; overflow: hidden; border: 1px solid var(--border-tags); border-radius: 10px; }
.rollup-dimension-filter-row { display: flex; align-items: center; gap: 24px; min-height: 62px; padding: 0 16px; border-bottom: 1px solid var(--border-tags); }
.rollup-dimension-filter-row:last-child { border-bottom: 0; }
.rollup-dimension-label { flex: 0 0 86px; color: var(--nl-color-weak); font-size: 18px; font-weight: 700; white-space: nowrap; }
.rollup-dimension-label i { margin-right: 4px; font-size: 13px; }
.rollup-all-option, .rollup-dimension-option, .rollup-more-option, .rollup-collapse-button { border: 0; background: transparent; cursor: pointer; }
.rollup-all-option { flex: 0 0 auto; min-width: 42px; padding: 4px 0; color: var(--nl-color); font-size: 16px; white-space: nowrap; }
.rollup-dimension-search { flex: 0 0 178px; width: 178px; }
.rollup-dimension-search :deep(.el-input__inner) { height: 36px; border-color: var(--border-tags); background: var(--bg-card); color: var(--nl-color); }
.rollup-dimension-options { display: flex; flex: 1 1 0; align-items: center; gap: 30px; min-width: 0; overflow: hidden; }
.rollup-dimension-option { flex: 0 0 auto; padding: 4px 0; color: var(--nl-color); font-size: 16px; line-height: 22px; white-space: nowrap; }
.rollup-all-option.selected, .rollup-dimension-option.selected, .rollup-more-option { color: var(--primary-color); }
.rollup-more-option { flex: 0 0 auto; font-size: 15px; white-space: nowrap; }
.rollup-dimension-filter-row.expanded { align-items: flex-start; padding-top: 12px; padding-bottom: 12px; }
.rollup-dimension-filter-row.expanded .rollup-dimension-label, .rollup-dimension-filter-row.expanded .rollup-all-option, .rollup-dimension-filter-row.expanded .rollup-dimension-search, .rollup-dimension-filter-row.expanded .rollup-more-option { margin-top: 5px; }
.rollup-dimension-filter-row.expanded .rollup-dimension-options { flex-wrap: wrap; row-gap: 10px; overflow: visible; }
.rollup-collapse-button { display: block; margin: 16px auto 0; color: var(--primary-color); font-weight: 600; }
.performance-rollup-page { display: flex; flex-direction: column; gap: var(--page-section-gap); box-sizing: border-box; height: calc(100% - 2px); min-height: 0; overflow: hidden; }
.performance-rollup-page .rollup-filter-panel { flex: 0 0 auto; }
.performance-rollup-page .performance-rollup-section { flex: 0 0 auto; min-height: 0; margin-bottom: 0; }
.performance-rollup-section { display: flex; flex-direction: column; padding: 16px 22px; }
.performance-rollup-header { display: flex; align-items: center; justify-content: space-between; min-height: 48px; margin-bottom: 16px; border-bottom: 1px solid var(--border-tags); }
.performance-rollup-header h2 { position: relative; margin: 0; padding-left: 20px; color: var(--nl-color-title); font-size: 22px; line-height: 1; }
.performance-rollup-header h2::before { position: absolute; top: -5px; left: 0; width: 5px; height: 28px; border-radius: 2px; background: var(--primary-color); content: ''; }
.performance-rollup-table { flex: 1 1 auto; min-height: 0; }
.performance-rollup-table :deep(.el-table__header th), .performance-rollup-table :deep(.el-table__body td), .performance-rollup-table :deep(.el-table__footer td) { border-color: var(--border-tags); }
.performance-rollup-table :deep(.el-table__header th) { background: var(--bg-table-header); color: var(--nl-color-weak); font-size: 16px; }
.performance-rollup-table :deep(.el-table__body td) { background: var(--bg-card); color: var(--nl-color); }
.performance-rollup-table :deep(.el-table__footer td) { background: var(--bg-hover); color: var(--nl-color-title); font-weight: 700; }
.performance-rollup-table :deep(.el-table__footer-wrapper), .performance-rollup-table :deep(.el-table__footer-wrapper table), .performance-rollup-table :deep(.el-table__footer-wrapper tbody), .performance-rollup-table :deep(.el-table__footer-wrapper tr), .performance-rollup-table :deep(.el-table__footer-wrapper td) { background-color: var(--bg-hover) !important; }
.performance-rollup-table :deep(.el-table__footer-wrapper td .cell) { color: var(--nl-color-title) !important; font-weight: 700; }
.performance-detail-link { color: var(--primary-color); }
@media (max-width: 768px) { .quality-summary { grid-template-columns: 1fr; } }
</style>
