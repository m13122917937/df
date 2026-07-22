<template>
  <div class="app-container analysis-config-page">
    <section class="config-card">
      <div class="title-row">
        <div>
          <h3>{{ pageTitle }}</h3>
          <p>{{ pageDescription }}</p>
          <p class="guide-text">{{ formGuide }}</p>
        </div>
        <div class="header-actions">
          <template v-if="!isMargin && !isCollectionCycle && !isWarehouseCostPage">
            <el-button icon="el-icon-download" @click="downloadTemplate">下载模板</el-button>
            <el-upload action="#" :show-file-list="false" :http-request="importFile" accept=".xlsx,.xls">
              <el-button icon="el-icon-upload2">导入</el-button>
            </el-upload>
            <el-button icon="el-icon-document" @click="openImportLogs">导入记录</el-button>
            <el-button icon="el-icon-download" @click="exportData">导出</el-button>
          </template>
          <el-button type="primary" icon="el-icon-plus" @click="openDialog()">新增{{ pageTitle }}</el-button>
        </div>
      </div>
      <el-form :inline="true" :model="query" size="small" :class="{ 'simple-query-form': isSimpleStoreConfig }">
        <el-form-item v-if="!isMargin && !isCollectionCycle" label="日期">
          <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item label="平台"><el-select v-if="isSimpleStoreConfig" v-model="query.platform" clearable filterable @change="query.shopName = ''"><el-option v-for="item in marginPlatformOptions" :key="item" :label="item" :value="item" /></el-select><el-input v-else v-model="query.platform" clearable /></el-form-item>
        <el-form-item label="店铺"><el-select v-if="isSimpleStoreConfig" v-model="query.shopName" clearable filterable><el-option v-for="item in marginShopOptions" :key="item.id" :label="item.keyWord" :value="item.keyWord" /></el-select><el-input v-else v-model="query.shopName" clearable /></el-form-item>
        <el-form-item v-if="!isMargin && !isCollectionCycle" label="货品编码"><el-input v-model="query.goodsNo" clearable /></el-form-item>
        <el-form-item :class="{ 'simple-query-actions': isSimpleStoreConfig }">
          <el-button type="primary" icon="el-icon-search" @click="loadData">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="config-card table-card">
      <el-table v-loading="loading" :data="pagedRows" border stripe>
        <template v-if="isMargin">
          <el-table-column prop="platform" label="平台" min-width="180" />
          <el-table-column prop="shopName" label="店铺名称" min-width="260" show-overflow-tooltip />
          <el-table-column :label="amountLabel" min-width="180" align="right"><template slot-scope="scope">{{ money(scope.row.marginAmount) }}</template></el-table-column>
          <el-table-column prop="updatedTime" label="操作时间" min-width="180" />
        </template>
        <template v-else-if="isCollectionCycle">
          <el-table-column prop="platform" label="平台" min-width="140" />
          <el-table-column prop="shopName" label="店铺名称" min-width="220" show-overflow-tooltip />
          <el-table-column prop="goodsCollectionDays" label="货款回款天数" min-width="140" />
          <el-table-column prop="subsidyCollectionDays" label="补贴款回款天数" min-width="150" />
          <el-table-column prop="nationalSubsidyCollectionDays" label="国补款回款天数" min-width="150" />
          <el-table-column prop="updatedTime" label="操作时间" min-width="180" />
        </template>
        <template v-else-if="isWarehouseCostPage">
          <el-table-column prop="monthValue" label="月份" min-width="180" />
          <el-table-column prop="afterSalesHeadcount" label="售后人力" min-width="180" />
          <el-table-column label="售后人力成本" min-width="220"><template slot-scope="scope">{{ money(scope.row.afterSalesLaborCost) }}</template></el-table-column>
          <el-table-column prop="updatedTime" label="操作时间" min-width="180" />
        </template>
        <template v-else>
          <el-table-column prop="businessDate" label="发生日期" width="110" />
          <el-table-column prop="monthValue" label="月份" width="90" />
          <el-table-column prop="platform" label="平台" width="100" />
          <el-table-column prop="shopName" label="店铺" min-width="140" show-overflow-tooltip />
          <el-table-column prop="originalOrderNo" label="订单号" min-width="150" show-overflow-tooltip />
          <el-table-column prop="goodsNo" label="货品编码" min-width="130" show-overflow-tooltip />
          <el-table-column prop="goodsName" label="商品名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="brand" label="品牌" width="110" />
          <el-table-column prop="category" label="品类" width="110" />
          <el-table-column :label="amountLabel" width="110" align="right"><template slot-scope="scope">{{ money(scope.row.amount) }}</template></el-table-column>
          <el-table-column v-if="showCoefficient" prop="coefficient" :label="coefficientLabel" width="110" align="right" />
          <el-table-column prop="reason" label="说明" min-width="160" show-overflow-tooltip />
        </template>
        <el-table-column label="操作" width="130" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" @click="openDialog(scope.row)">修改</el-button>
            <el-button v-if="!isCollectionCycle" type="text" class="danger" @click="remove(scope.row)">删除</el-button>
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

    <el-dialog :title="`${form.id ? '编辑' : '新增'}${pageTitle}`" :visible.sync="dialogVisible" :width="isSimpleStoreConfig ? '620px' : isWarehouseCostPage ? '800px' : '720px'" :class="{ 'simple-config-dialog': isSimpleStoreConfig, 'warehouse-cost-dialog': isWarehouseCostPage }" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isSimpleStoreConfig ? '160px' : '100px'">
        <el-row :gutter="16">
          <template v-if="isMargin">
            <el-col :span="24"><el-form-item label="平台" prop="platform"><el-select v-model="form.platform" filterable placeholder="请选择平台" @change="handleMarginPlatformChange"><el-option v-for="item in marginPlatformOptions" :key="item" :label="item" :value="item" /></el-select></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="店铺名称" prop="shopName"><el-select v-model="form.shopName" filterable placeholder="请选择店铺名称"><el-option v-for="item in marginShopOptions" :key="item.id" :label="item.keyWord" :value="item.keyWord" /></el-select></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="保证金金额" prop="marginAmount"><el-input-number v-model="form.marginAmount" :min="0" :precision="2" :controls="false" /></el-form-item></el-col>
          </template>
          <template v-else-if="isCollectionCycle">
            <el-col :span="24"><el-form-item label="平台" prop="platform"><el-select v-model="form.platform" filterable @change="handleMarginPlatformChange"><el-option v-for="item in marginPlatformOptions" :key="item" :label="item" :value="item" /></el-select></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="店铺名称" prop="shopName"><el-select v-model="form.shopName" filterable><el-option v-for="item in marginShopOptions" :key="item.id" :label="item.keyWord" :value="item.keyWord" /></el-select></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="货款回款天数" prop="goodsCollectionDays"><el-input-number v-model="form.goodsCollectionDays" :min="0" :precision="0" :controls="false" /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="补贴款回款天数" prop="subsidyCollectionDays"><el-input-number v-model="form.subsidyCollectionDays" :min="0" :precision="0" :controls="false" /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="国补款回款天数" prop="nationalSubsidyCollectionDays"><el-input-number v-model="form.nationalSubsidyCollectionDays" :min="0" :precision="0" :controls="false" /></el-form-item></el-col>
          </template>
          <template v-else-if="isWarehouseCostPage">
            <el-col :span="24"><el-form-item label="月份" prop="monthValue"><el-date-picker v-model="form.monthValue" type="month" value-format="yyyy-MM" /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="售后人力" prop="afterSalesHeadcount"><el-input-number v-model="form.afterSalesHeadcount" :min="0" :precision="2" :controls="false" /></el-form-item></el-col>
            <el-col :span="24"><el-form-item label="售后人力成本" prop="afterSalesLaborCost"><el-input-number v-model="form.afterSalesLaborCost" :min="0" :precision="2" :controls="false" /></el-form-item></el-col>
          </template>
          <template v-else>
          <el-col v-if="!isMonthlyCost" :span="12"><el-form-item label="发生日期" prop="businessDate"><el-date-picker v-model="form.businessDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="isMonthlyCost ? '核算月份' : '归属月份'"><el-date-picker v-model="form.monthValue" type="month" value-format="yyyy-MM" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="平台"><el-input v-model="form.platform" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="店铺"><el-input v-model="form.shopName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="订单号"><el-input v-model="form.originalOrderNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="货品编码"><el-input v-model="form.goodsNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="商品名称"><el-input v-model="form.goodsName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="品类"><el-input v-model="form.category" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item :label="amountLabel"><el-input-number v-model="form.amount" :precision="4" :controls="false" /></el-form-item></el-col>
          <el-col v-if="showCoefficient" :span="12"><el-form-item :label="coefficientLabel"><el-input-number v-model="form.coefficient" :precision="8" :controls="false" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生效开始"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="生效结束"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" /></el-form-item></el-col>
          <el-col v-if="isCapitalCost" :span="12"><el-form-item label="年化资金成本率"><el-input-number v-model="form.annualRatePercent" :precision="2" :min="0" :max="100" :controls="false" /><span class="field-unit">%</span></el-form-item></el-col>
          <el-col v-if="isPeopleCost" :span="12"><el-form-item label="成本归属"><el-select v-model="form.costScope"><el-option label="直接人员成本" value="DIRECT" /><el-option label="部门直接费用" value="DEPARTMENT" /><el-option label="间接人员成本" value="INDIRECT" /></el-select></el-form-item></el-col>
          <el-col v-if="isPeopleCost" :span="12"><el-form-item label="人员数量"><el-input-number v-model="form.headcount" :precision="2" :min="0" :controls="false" /></el-form-item></el-col>
          <el-col v-if="isWarehouseCost" :span="12"><el-form-item label="成本归属"><el-select v-model="form.costScope"><el-option label="直接仓配成本" value="DIRECT" /><el-option label="间接仓配成本" value="INDIRECT" /></el-select></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="说明"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item></el-col>
          </template>
        </el-row>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog title="配置导入记录" :visible.sync="importLogVisible" width="900px" append-to-body>
      <el-table v-loading="importLogLoading" :data="importLogs" border size="small">
        <el-table-column prop="createdTime" label="导入时间" width="170" />
        <el-table-column prop="fileName" label="文件名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="configType" label="配置类型" width="130" />
        <el-table-column prop="totalCount" label="总行数" width="80" align="right" />
        <el-table-column prop="successCount" label="成功" width="80" align="right" />
        <el-table-column prop="failedCount" label="失败" width="80" align="right" />
        <el-table-column label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'SUCCESS' ? 'success' : 'danger'" size="mini">
              {{ scope.row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误原因" min-width="180" show-overflow-tooltip />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { deleteAnalysisConfig, deleteAnalysisMargin, getAnalysisCollectionCycleList, getAnalysisConfigList, getAnalysisImportLogs, getAnalysisMarginList, getAnalysisWarehouseCostList, importAnalysisConfig, saveAnalysisCollectionCycle, saveAnalysisConfig, saveAnalysisMargin, saveAnalysisWarehouseCost } from '@/api/analysis'
import { getPayerConfigListApi } from '@/api/monery'

export default {
  name: 'AnalysisConfig',
  data() {
    return {
      loading: false,
      saving: false,
      dialogVisible: false,
      importLogVisible: false,
      importLogLoading: false,
      importLogs: [],
      payerShopConfigs: [],
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
        fixedCoefficient: 'FIXED_COEFFICIENT', penalty: 'PENALTY',
        promotion: 'PROMOTION', margin: 'MARGIN', collectionDays: 'COLLECTION_DAYS',
        internalCost: 'INTERNAL_COST', warehouseCost: 'WAREHOUSE_COST', shopWhitelist: 'SHOP_WHITELIST',
        platformFee: 'PLATFORM_FEE', logisticsFee: 'LOGISTICS', tax: 'TAX',
        otherAdjustment: 'OTHER_ADJUSTMENT'
      }
      const key = Object.keys(mapping).find(item => this.$route.path.indexOf(item) !== -1)
      return key ? mapping[key] : 'FIXED_COEFFICIENT'
    },
    pageTitle() { return this.$route.meta.title || '核算配置' },
    pageDescription() {
      const descriptions = {
        FIXED_COEFFICIENT: '按商品、平台和店铺设置成本计算规则。',
        PLATFORM_FEE: '记录平台服务、技术服务等经营费用。',
        LOGISTICS: '记录发货、快递和物流相关费用。',
        PROMOTION: '记录店铺和商品的推广投放费用。',
        TAX: '记录经营产生的税费。',
        OTHER_ADJUSTMENT: '记录无法归入其他类别的收入或费用调整。',
        MARGIN: '选择已配置的平台与店铺，录入当前保证金金额。',
        COLLECTION_DAYS: '设置回款周期和资金占用成本。',
        INTERNAL_COST: '记录运营、采购、客服等人员成本。',
        WAREHOUSE_COST: '记录仓储和配送相关成本。'
      }
      return descriptions[this.configType] || '设置将用于经营利润核算。'
    },
    isCollectionDays() { return this.configType === 'COLLECTION_DAYS' },
    isCollectionCycle() { return this.configType === 'COLLECTION_DAYS' },
    isMargin() { return this.configType === 'MARGIN' },
    isSimpleStoreConfig() { return this.isMargin || this.isCollectionCycle },
    isCapitalCost() { return this.configType === 'COLLECTION_DAYS' },
    isMonthlyCost() { return ['INTERNAL_COST', 'WAREHOUSE_COST'].includes(this.configType) },
    isPeopleCost() { return this.configType === 'INTERNAL_COST' },
    isWarehouseCost() { return this.configType === 'WAREHOUSE_COST' },
    isWarehouseCostPage() { return this.configType === 'WAREHOUSE_COST' },
    showCoefficient() { return ['FIXED_COEFFICIENT', 'COLLECTION_DAYS'].includes(this.configType) },
    amountLabel() {
      const labels = { MARGIN: '保证金', INTERNAL_COST: '月度成本', WAREHOUSE_COST: '月度成本' }
      return labels[this.configType] || '金额'
    },
    coefficientLabel() { return this.isCollectionDays ? '回款天数' : '费用系数' },
    formGuide() {
      const guides = {
        MARGIN: '保证金仅维护平台、店铺名称和保证金金额。',
        COLLECTION_DAYS: '填写回款天数和年化资金成本率，系统按销售收入计算资金占用。',
        INTERNAL_COST: '按月录入人员成本、人数与归属，系统分配至直接或间接人力。',
        WAREHOUSE_COST: '按月录入仓配成本并选择直接或间接归属。'
      }
      return guides[this.configType] || '可按平台、店铺、订单或货品限定适用范围；未填写的维度视为通用规则。'
    },
    marginPlatformOptions() { return [...new Set(this.payerShopConfigs.map(item => item.platform).filter(Boolean))] },
    marginShopOptions() { return this.payerShopConfigs.filter(item => !this.form.platform || item.platform === this.form.platform) },
    formRules() {
      if (this.isMargin) {
        return {
          platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
          shopName: [{ required: true, message: '请选择店铺名称', trigger: 'change' }],
          marginAmount: [{ required: true, message: '请输入保证金金额', trigger: 'blur' }]
        }
      }
      if (this.isCollectionCycle) {
        return {
          platform: [{ required: true, message: '请选择平台', trigger: 'change' }],
          shopName: [{ required: true, message: '请选择店铺名称', trigger: 'change' }],
          goodsCollectionDays: [{ required: true, message: '请输入货款回款周期', trigger: 'blur' }],
          subsidyCollectionDays: [{ required: true, message: '请输入补贴款回款周期', trigger: 'blur' }],
          nationalSubsidyCollectionDays: [{ required: true, message: '请输入国补回款周期', trigger: 'blur' }]
        }
      }
      if (this.isWarehouseCostPage) {
        return { monthValue: [{ required: true, message: '请选择月份', trigger: 'change' }], afterSalesHeadcount: [{ required: true, message: '请输入售后人力', trigger: 'blur' }], afterSalesLaborCost: [{ required: true, message: '请输入售后人力成本', trigger: 'blur' }] }
      }
      return this.isMonthlyCost ? {} : this.rules
    },
    pagedRows() { return this.rows.slice((this.pageNum - 1) * this.pageSize, this.pageNum * this.pageSize) }
  },
  watch: {
    '$route.fullPath'() { this.loadData() }
  },
  created() {
    this.loadData()
    this.loadPayerShopConfigs()
  },
  methods: {
    async loadPayerShopConfigs() {
      try {
        const response = await getPayerConfigListApi({ pageNum: 1, pageSize: 1000 })
        this.payerShopConfigs = response.code === 200 ? (response.rows || []) : []
      } catch (error) {
        this.payerShopConfigs = []
        this.$message.error('店铺配置加载失败')
      }
    },
    async loadData() {
      this.loading = true
      try {
        const params = Object.assign({}, this.query, {
          configType: this.configType,
          startDate: this.dateRange && this.dateRange[0],
          endDate: this.dateRange && this.dateRange[1]
        })
        const response = this.isMargin
          ? await getAnalysisMarginList({ platform: this.query.platform, shopName: this.query.shopName })
          : this.isCollectionCycle
            ? await getAnalysisCollectionCycleList({ platform: this.query.platform, shopName: this.query.shopName })
            : this.isWarehouseCostPage
              ? await getAnalysisWarehouseCostList({})
            : await getAnalysisConfigList(params)
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
        goodsNo: '', goodsName: '', brand: '', category: '', amount: null, marginAmount: null, coefficient: null,
        goodsCollectionDays: null, subsidyCollectionDays: null, nationalSubsidyCollectionDays: null, afterSalesHeadcount: null, afterSalesLaborCost: null,
        startDate: '', endDate: '', reason: '', extraData: '', annualRatePercent: null, costScope: '', headcount: null
      }, row || {}, { configType: this.configType })
      this.fillBusinessFields()
      if (this.isMargin || this.isCollectionCycle) {
        this.form.businessDate = ''
        this.form.monthValue = ''
        this.form.startDate = ''
        this.form.endDate = ''
        this.form.annualRatePercent = null
        this.form.costScope = ''
        this.form.headcount = null
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    submit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          await (this.isMargin ? saveAnalysisMargin(this.buildPayload()) : this.isCollectionCycle ? saveAnalysisCollectionCycle(this.buildPayload()) : this.isWarehouseCostPage ? saveAnalysisWarehouseCost(this.buildPayload()) : saveAnalysisConfig(this.buildPayload()))
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.loadData()
        } finally { this.saving = false }
      })
    },
    async remove(row) {
      await this.$confirm('确认删除该核算配置吗？', '删除确认', { type: 'warning' })
      await (this.isMargin ? deleteAnalysisMargin(row.id) : deleteAnalysisConfig(row.id))
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
        const message = error.msg || (error.response && error.response.data && error.response.data.msg) || '导入失败'
        if (message.indexOf('重复配置') === -1) {
          this.$message.error(message)
          return
        }
        await this.$confirm('导入数据中存在重复配置，是否覆盖已有记录？', '覆盖确认', { type: 'warning' })
        await importAnalysisConfig(this.configType, true, option.file)
      }
      this.$message.success('导入成功')
      this.loadData()
    },
    async openImportLogs() {
      this.importLogVisible = true
      this.importLogLoading = true
      try {
        const response = await getAnalysisImportLogs(50)
        this.importLogs = response.data || []
      } finally {
        this.importLogLoading = false
      }
    },
    fillBusinessFields() {
      let data = {}
      try { data = this.form.extraData ? JSON.parse(this.form.extraData) : {} } catch (error) { data = {} }
      this.form.annualRatePercent = data.annualRate == null ? null : Number(data.annualRate) * 100
      this.form.costScope = data.costScope || ''
      this.form.headcount = data.headcount == null ? null : Number(data.headcount)
    },
    buildPayload() {
      if (this.isMargin) {
        return {
          id: this.form.id,
          platform: this.form.platform,
          shopName: this.form.shopName,
          marginAmount: this.form.marginAmount
        }
      }
      if (this.isCollectionCycle) {
        return {
          id: this.form.id,
          platform: this.form.platform,
          shopName: this.form.shopName,
          goodsCollectionDays: this.form.goodsCollectionDays,
          subsidyCollectionDays: this.form.subsidyCollectionDays,
          nationalSubsidyCollectionDays: this.form.nationalSubsidyCollectionDays
        }
      }
      if (this.isWarehouseCostPage) {
        return { id: this.form.id, monthValue: this.form.monthValue, afterSalesHeadcount: this.form.afterSalesHeadcount, afterSalesLaborCost: this.form.afterSalesLaborCost }
      }
      const payload = Object.assign({}, this.form)
      const extraData = {}
      if (this.isCapitalCost && payload.annualRatePercent != null) extraData.annualRate = Number(payload.annualRatePercent) / 100
      if ((this.isPeopleCost || this.isWarehouseCost) && payload.costScope) extraData.costScope = payload.costScope
      if (this.isPeopleCost && payload.headcount != null) extraData.headcount = Number(payload.headcount)
      payload.extraData = Object.keys(extraData).length ? JSON.stringify(extraData) : ''
      delete payload.annualRatePercent
      delete payload.costScope
      delete payload.headcount
      return payload
    },
    handleMarginPlatformChange() {
      this.form.shopName = ''
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
.title-row .guide-text { margin-top: 6px; font-size: 13px; }
.header-actions { display: flex; align-items: center; gap: 8px; }
.simple-query-form { display: flex; align-items: center; width: 100%; }
.simple-query-actions { margin-left: auto; }
.simple-config-dialog .el-form-item { margin-bottom: 18px; }
.simple-config-dialog ::v-deep .el-form-item__label { white-space: nowrap; }
.simple-config-dialog .el-select,
.simple-config-dialog .el-input-number { width: 100%; }
.simple-config-dialog .el-input-number ::v-deep .el-input { width: 100%; }
.simple-config-dialog .el-input-number ::v-deep .el-input__inner { padding-left: 8px; padding-right: 8px; }
.warehouse-cost-dialog .el-input-number,
.warehouse-cost-dialog .el-date-picker { width: 100%; }
.warehouse-cost-dialog .el-input-number ::v-deep .el-input { width: 100%; }
.danger { color: #f56c6c; }
.field-unit { margin-left: 8px; color: var(--nl-color-tip); }
.el-pagination { margin-top: 16px; text-align: right; }
</style>
