<template>
  <div class="app-container analysis-config-page">
    <section class="config-card">
      <div class="title-row">
        <div>
          <h3>{{ pageTitle }}</h3>
          <p>配置仅保存到经营分析模块的新表，不影响原业务数据。</p>
        </div>
        <div class="header-actions">
          <el-button icon="el-icon-download" @click="downloadTemplate">下载模板</el-button>
          <el-upload action="#" :show-file-list="false" :http-request="importFile" accept=".xlsx,.xls">
            <el-button icon="el-icon-upload2">导入</el-button>
          </el-upload>
          <el-button icon="el-icon-download" @click="exportData">导出</el-button>
          <el-button type="primary" icon="el-icon-plus" @click="openDialog()">新增配置</el-button>
        </div>
      </div>
      <el-form :inline="true" :model="query" size="small">
        <el-form-item label="日期">
          <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item label="平台"><el-input v-model="query.platform" clearable /></el-form-item>
        <el-form-item label="店铺"><el-input v-model="query.shopName" clearable /></el-form-item>
        <el-form-item label="货品编码"><el-input v-model="query.goodsNo" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="config-card table-card">
      <el-table v-loading="loading" :data="pagedRows" border stripe>
        <el-table-column prop="businessDate" label="发生日期" width="110" />
        <el-table-column prop="monthValue" label="月份" width="90" />
        <el-table-column prop="platform" label="平台" width="100" />
        <el-table-column prop="shopName" label="店铺" min-width="140" show-overflow-tooltip />
        <el-table-column prop="originalOrderNo" label="订单号" min-width="150" show-overflow-tooltip />
        <el-table-column prop="goodsNo" label="货品编码" min-width="130" show-overflow-tooltip />
        <el-table-column prop="goodsName" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" width="110" />
        <el-table-column prop="category" label="品类" width="110" />
        <el-table-column label="金额" width="110" align="right"><template slot-scope="scope">{{ money(scope.row.amount) }}</template></el-table-column>
        <el-table-column prop="coefficient" label="系数/点位" width="110" align="right" />
        <el-table-column prop="reason" label="说明" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="130" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="openDialog(scope.row)">编辑</el-button>
            <el-button type="text" class="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="rows.length > pageSize"
        :current-page.sync="pageNum"
        :page-size="pageSize"
        :total="rows.length"
        layout="prev, pager, next, total"
      />
    </section>

    <el-dialog :title="`${form.id ? '编辑' : '新增'}${pageTitle}`" :visible.sync="dialogVisible" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="发生日期" prop="businessDate"><el-date-picker v-model="form.businessDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="月份"><el-date-picker v-model="form.monthValue" type="month" value-format="yyyy-MM" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="平台"><el-input v-model="form.platform" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="店铺"><el-input v-model="form.shopName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="订单号"><el-input v-model="form.originalOrderNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="货品编码"><el-input v-model="form.goodsNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="商品名称"><el-input v-model="form.goodsName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品类"><el-input v-model="form.category" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="金额"><el-input-number v-model="form.amount" :precision="4" :controls="false" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="系数/点位"><el-input-number v-model="form.coefficient" :precision="8" :controls="false" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生效开始"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生效结束"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="说明"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { deleteAnalysisConfig, getAnalysisConfigList, importAnalysisConfig, saveAnalysisConfig } from '@/api/analysis'

export default {
  name: 'AnalysisConfig',
  data() {
    return {
      loading: false,
      saving: false,
      dialogVisible: false,
      dateRange: [],
      query: { platform: '', shopName: '', goodsNo: '' },
      rows: [],
      pageNum: 1,
      pageSize: 20,
      form: {},
      rules: { businessDate: [{ required: true, message: '请选择发生日期', trigger: 'change' }] }
    }
  },
  computed: {
    configType() {
      const mapping = {
        rebateProtection: 'REBATE', rebateDetail: 'REBATE', fixedCoefficient: 'FIXED_COEFFICIENT', cashback: 'CASHBACK', penalty: 'PENALTY',
        promotion: 'PROMOTION', margin: 'MARGIN', collectionDays: 'COLLECTION_DAYS',
        internalCost: 'INTERNAL_COST', warehouseCost: 'WAREHOUSE_COST', shopWhitelist: 'SHOP_WHITELIST'
      }
      const key = Object.keys(mapping).find(item => this.$route.path.indexOf(item) !== -1)
      return key ? mapping[key] : 'FIXED_COEFFICIENT'
    },
    pageTitle() { return this.$route.meta.title || '核算配置' },
    pagedRows() { return this.rows.slice((this.pageNum - 1) * this.pageSize, this.pageNum * this.pageSize) }
  },
  watch: {
    '$route.fullPath'() { this.loadData() }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = Object.assign({}, this.query, {
          configType: this.configType,
          startDate: this.dateRange && this.dateRange[0],
          endDate: this.dateRange && this.dateRange[1]
        })
        const response = await getAnalysisConfigList(params)
        this.rows = response.data || []
        this.pageNum = 1
      } finally { this.loading = false }
    },
    resetQuery() {
      this.dateRange = []
      this.query = { platform: '', shopName: '', goodsNo: '' }
      this.loadData()
    },
    openDialog(row) {
      this.form = Object.assign({
        configType: this.configType,
        businessDate: '', monthValue: '', platform: '', shopName: '', originalOrderNo: '',
        goodsNo: '', goodsName: '', brand: '', category: '', amount: null, coefficient: null,
        startDate: '', endDate: '', reason: ''
      }, row || {}, { configType: this.configType })
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          await saveAnalysisConfig(this.form)
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.loadData()
        } finally { this.saving = false }
      })
    },
    async remove(row) {
      await this.$confirm('确认删除该核算配置吗？', '删除确认', { type: 'warning' })
      await deleteAnalysisConfig(row.id)
      this.$message.success('删除成功')
      this.loadData()
    },
    downloadTemplate() {
      this.download('/analysis/config/template', {}, '经营核算配置导入模板.xlsx')
    },
    exportData() {
      const params = Object.assign({}, this.query, {
        configType: this.configType,
        startDate: this.dateRange && this.dateRange[0],
        endDate: this.dateRange && this.dateRange[1]
      })
      this.download('/analysis/config/export', params, `${this.pageTitle}.xlsx`)
    },
    async importFile(option) {
      try {
        await importAnalysisConfig(this.configType, false, option.file)
      } catch (error) {
        await this.$confirm('导入数据中存在重复配置，是否覆盖已有记录？', '覆盖确认', { type: 'warning' })
        await importAnalysisConfig(this.configType, true, option.file)
      }
      this.$message.success('导入成功')
      this.loadData()
    },
    money(value) { return value == null ? '-' : `¥${Number(value).toFixed(2)}` }
  }
}
</script>

<style lang="scss" scoped>
.analysis-config-page { min-height: 100%; background: var(--bg-page); color: var(--nl-color); }
.config-card { background: var(--bg-card); border: 1px solid var(--border-tags); border-radius: var(--radius); padding: var(--page-card-padding); margin-bottom: var(--page-section-gap); box-shadow: var(--shadow-card); }
.title-row { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.title-row h3 { margin: 0 0 7px; color: var(--nl-color-title); }
.title-row p { margin: 0; color: var(--nl-color-tip); }
.header-actions { display: flex; align-items: center; gap: 8px; }
.danger { color: #f56c6c; }
.el-pagination { margin-top: 16px; text-align: right; }
</style>
