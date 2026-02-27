<template>
  <div class="all-orders-page">
    <!-- 搜索面板 -->
    <div class="search-card">
      <el-form ref="searchFormRef" :model="searchForm" size="small">
        <div class="search-content">
          <div class="search-container">
            <div class="search-row">
              <div class="search-item">
                <div class="search-label-dropdown">
                  <el-dropdown trigger="click" @command="handleSearchTypeChange" @visible-change="handleSearchDropdownVisible">
                    <span class="search-label-select">
                      {{ currentSearchType.label }}
                      <i class="el-icon-arrow-down el-icon--right" :class="{ 'is-rotate': searchDropdownVisible }"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item 
                        v-for="item in searchTypeOptions" 
                        :key="item.value" 
                        :command="item.value"
                        :class="{ 'is-active': currentSearchType.value === item.value }"
                      >
                        {{ item.label }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
                <el-input 
                  v-model="searchForm.orderCodeInput" 
                  :placeholder="'请输入' + currentSearchType.label" 
                  class="search-input" 
                  clearable
                />
              </div>
              <div class="search-item">
                <div class="search-label-dropdown">
                  <el-dropdown trigger="click" @command="handleTimeTypeChange" @visible-change="handleTimeDropdownVisible">
                    <span class="search-label-select">
                      {{ currentTimeType.label }}
                      <i class="el-icon-arrow-down el-icon--right" :class="{ 'is-rotate': timeDropdownVisible }"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item 
                        v-for="item in timeTypeOptions" 
                        :key="item.value" 
                        :command="item.value"
                        :class="{ 'is-active': currentTimeType.value === item.value }"
                      >
                        {{ item.label }}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
                <el-date-picker
                  v-model="searchForm.timeRange"
                  type="daterange"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  range-separator="至"
                  value-format="yyyy-MM-dd"
                  unlink-panels
                  :picker-options="timePickerOptions"
                  @change="handleTimeRangeChange"
                  class="search-input"
                />
              </div>
            </div>                            
          </div>

          <div class="search-actions">
            <div class="primary-actions">
              <el-button type="primary" icon="el-icon-search" @click="handleSearch" class="search-action-btn">
                搜索
              </el-button>
              <el-button icon="el-icon-refresh-left" @click="handleReset" class="reset-btn">
                重置
              </el-button>
              <el-button type="success" icon="el-icon-download" @click="handleExport" :loading="exportLoading" class="export-btn">
                导出
              </el-button>
            </div>
          </div>
        </div>
        <div>
          <div class="secondary-actions">
            <el-button type="success" icon="el-icon-document-copy" @click="openBatchDialog" class="batch-btn">
              批量内部单号查询
            </el-button>
            <el-button type="warning" icon="el-icon-document" @click="openBatchMerchantDialog" class="batch-btn">
              批量商家单号查询
            </el-button>
            <el-button type="info" icon="el-icon-document" @click="openBatchErpOrderDialog" class="batch-btn">
              批量旺店通单号查询
            </el-button>
          </div>
        </div>
      </el-form>
    </div>
    <!--表格 -->
    <div class="table-wrapper table-section">
      <el-table :data="tableData" v-loading="loading" border stripe size="small" style="width: 100%" height="100%"
        :header-cell-style="headerCellStyle" :cell-style="{ padding: '8px 0' }">
        <template slot="empty">
          <EmptyState text="暂无全部订单数据" />
        </template>

        <el-table-column label="单号" min-width="260" fixed="left">
          <template slot-scope="scope">
            <div class="order-product order-numbers">
              <div class="order-number-item">
                内部:
                <span>{{ scope.row.orderCode || '-' }}</span>
                <i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon"
                  @click="copyText(scope.row.orderCode)" />
              </div>
              <div class="order-number-item">
                旺店通:
                <span>{{ scope.row.erpOrderId || '-' }}</span>
                <i v-if="scope.row.erpOrderId" class="el-icon-copy-document copy-icon"
                  @click="copyText(scope.row.erpOrderId)" />
              </div>
              <div class="order-number-item">
                商家:
                <span>{{ scope.row.originalOrderId || '-' }}</span>
                <i v-if="scope.row.originalOrderId" class="el-icon-copy-document copy-icon"
                  @click="copyText(scope.row.originalOrderId)" />
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="产品信息" min-width="240">
          <template slot-scope="scope">
            <div class="order-product">
              <div class="order-product-line">
                品牌/品类：{{ scope.row.brand || '-' }} / {{ scope.row.category || '-' }}
              </div>
              <div class="order-product-line">
                商品：{{ scope.row.productName || '-' }}
              </div>
              <div class="order-product-line">
                SKU：{{ scope.row.skuName || '-' }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="采购信息" min-width="250" >
          <template slot-scope="scope">
            <div class="order-product">公司：{{ scope.row.companyName || '--' }}</div>
            <div class="order-product">方式：{{ scope.row.tradeTypeName || '一件代发' }}</div>
          </template>
        </el-table-column>

        <el-table-column label="店铺" min-width="200" >
          <template slot-scope="scope">
            <div class="supplier-line">平台：{{ scope.row.platform || '-' }}</div>
            <div class="supplier-line">店铺：{{ scope.row.shopName || '-' }}</div>
          </template>
        </el-table-column>


        <el-table-column label="收件信息" min-width="220" align="center">
          <template slot-scope="scope">
            <el-popover placement="top" trigger="hover" popper-class="addr-popover">
              <div class="addr-content">
                <div class="addr-line main">收件人：{{ scope.row.addressee || '无' }}</div>
                <div class="addr-line sub">电话：{{ scope.row.phone || '无' }}</div>
                <div class="addr-line sub">
                  地址：{{ scope.row.receivingAddress || '无' }}
                  <i v-if="scope.row.receivingAddress" class="el-icon-copy-document copy-icon"
                    @click="handleCopyAddress(scope.row)" />
                </div>
              </div>
              <span slot="reference" class="order-product">
                {{ scope.row.provinceName || '-' }} {{ scope.row.cityName || '' }}
              </span>
            </el-popover>
          </template>
        </el-table-column>

        <el-table-column label="数量" prop="quantity" min-width="100" align="center" class="order-product"/>

        <el-table-column label="成交价" min-width="160" align="center">
          <template slot-scope="scope">
            <div class="order-product">{{ scope.row.tradePrice || '-' }}</div>
          </template>
        </el-table-column>


        <el-table-column label="订单状态" min-width="150" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)" size="small" effect="dark">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="时间信息" min-width="260">
          <template slot-scope="scope">
            <div class="time-line">
              发货：{{ scope.row.sendTime || '--' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" min-width="200" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="mini" @click="handleViewSerial(scope.row)">
              查看串码
            </el-button>
            <!-- <el-button type="text" size="mini" @click="handleViewLogistics(scope.row)">
              查看物流
            </el-button> -->
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination :current-page="pagination.currentPage" :page-size="pagination.pageSize" :total="pagination.total"
        :page-sizes="[30, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>

    <!-- 串码弹窗 -->
    <ImeiDialog :visible.sync="imeiDialogVisible" :current-order="currentOrder" />

    <!-- 物流弹窗 -->
    <LogisticsDialog :visible.sync="logisticsDialogVisible" :current-order="currentOrder" />

    <!-- 批量查询 -->
    <BatchQueryDialog
      :visible.sync="batchDialogVisible"
      :input-value.sync="batchOrderCodeInput"
      title="批量内部单号查询"
      placeholder="每行输入一个内部单号，最多可输入200条"
      @confirm="applyBatchSearch"
      @cancel="closeBatchDialog"
    />
    <!-- 批量商家单号查询 -->
    <BatchQueryDialog
      :visible.sync="batchMerchantDialogVisible"
      :input-value.sync="batchMerchantCodeInput"
      title="批量商家单号查询"
      placeholder="每行输入一个商家单号，最多可输入200条"
      @confirm="applyBatchMerchantSearch"
      @cancel="closeBatchMerchantDialog"
    />
    <!-- 批量旺店通单号查询 -->
    <BatchQueryDialog
      :visible.sync="batchErpOrderDialogVisible"
      :input-value.sync="batchErpOrderCodeInput"
      title="批量旺店通单号查询"
      placeholder="每行输入一个旺店通单号，最多可输入200条"
      @confirm="applyBatchErpOrderSearch"
      @cancel="closeBatchErpOrderDialog"
    />
  </div>
</template>

<script>
import EmptyState from '@/views/demandManage/wholesale/components/emptyState.vue'
import PriceChips from '@/views/demandManage/wholesale/components/priceChips.vue'
import ImeiDialog from '@/views/demandManage/wholesale/components/imeiDialog.vue'
import LogisticsDialog from '@/views/demandManage/wholesale/components/logisticsDialog.vue'
import BatchQueryDialog from './components/BatchQueryDialog.vue'
import {
  getAllOrderListApi,
  getAllOrderExportApi
} from '@/api/wholesale'
import {
  createCopyTextMethod,
  createFormatDateTimeMethod,
  formatAccountingPeriod
} from '@/utils/wholesaleUtils'
import { saveAs } from 'file-saver'
import { blobValidate } from '@/utils/ruoyi'

const getDefaultSearchForm = () => {
  return {
    orderCodeInput: '',
    timeRange: []
  }
}

export default {
  name: 'AllOrdersPage',
  components: {
    EmptyState,
    PriceChips,
    ImeiDialog,
    LogisticsDialog,
    BatchQueryDialog
  },
  data() {
    return {
      loading: false,
      exportLoading: false,
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0
      },
      // 下拉菜单展开状态
      searchDropdownVisible: false,
      timeDropdownVisible: false,
      // 搜索类型选项
      searchTypeOptions: [
        { label: '无仓单号', value: 'noWarehouseOrderCode', field: 'noWarehouseOrderCode' },
        { label: '商家单号', value: 'originalOrderId', field: 'originalOrderId' },
        { label: 'ERP单号', value: 'erpOrderId', field: 'erpOrderId' }
      ],
      currentSearchType: { label: '无仓单号', value: 'noWarehouseOrderCode', field: 'noWarehouseOrderCode' },
      // 时间类型选项
      timeTypeOptions: [
        { label: '创建时间', value: 'createTime', startField: 'createStartTime', endField: 'createEndTime' },
        { label: '导单时间', value: 'importTime', startField: 'importStartTime', endField: 'importEndTime' },
        { label: '成交时间', value: 'dealTime', startField: 'dealStartTime', endField: 'dealEndTime' },
        { label: '发货时间', value: 'sendTime', startField: 'sendStartTime', endField: 'sendEndTime' },
        { label: '签收时间', value: 'signTime', startField: 'signStartTime', endField: 'signEndTime' }
      ],
      currentTimeType: { label: '创建时间', value: 'createTime', startField: 'createStartTime', endField: 'createEndTime' },
      timePickerOptions: {
        disabledDate: (time) => {
          // 禁用未来日期
          const today = new Date()
          today.setHours(23, 59, 59, 999)
          if (time.getTime() > today.getTime()) {
            return true
          }
          // 如果已选择开始日期，限制结束日期不能超过开始日期后一个月
          if (this.searchForm.timeRange && this.searchForm.timeRange.length > 0 && this.searchForm.timeRange[0]) {
            const startDate = new Date(this.searchForm.timeRange[0])
            startDate.setHours(0, 0, 0, 0)
            const oneMonthLater = new Date(startDate)
            oneMonthLater.setMonth(oneMonthLater.getMonth() + 1)
            oneMonthLater.setHours(23, 59, 59, 999)
            // 禁用超过开始日期一个月后的日期
            return time.getTime() > oneMonthLater.getTime()
          }
          return false
        }
      },
      searchForm: getDefaultSearchForm(),
      batchDialogVisible: false,
      batchOrderCodeInput: '',
      batchOrderCodeList: [],
      batchMerchantDialogVisible: false,
      batchMerchantCodeInput: '',
      batchMerchantCodeList: [],
      batchErpOrderDialogVisible: false,
      batchErpOrderCodeInput: '',
      batchErpOrderCodeList: [],
      currentOrder: null,
      imeiDialogVisible: false,
      logisticsDialogVisible: false
    }
  },
  created() {
    // 初始化默认搜索条件
    this.searchForm = getDefaultSearchForm()
  },
  mounted() {
    this.fetchOrderList()
  },
  methods: {
    formatDateTime: createFormatDateTimeMethod(),
    copyText: createCopyTextMethod(this),
    formatAccountingPeriod,
    formatAccountingPeriodType(val) {
      if (val === null || val === undefined || val === '') {
        return '-'
      }
      const n = Number(val)
      if (!isFinite(n) || n < 0) return '-'
      if (n === 0) return '现款'
      if (n >= 1 && n <= 5) return `T+${n}`
      return '-'
    },
    // 处理搜索下拉菜单显示状态
    handleSearchDropdownVisible(visible) {
      this.searchDropdownVisible = visible
    },
    // 处理时间下拉菜单显示状态
    handleTimeDropdownVisible(visible) {
      this.timeDropdownVisible = visible
    },
    // 处理搜索类型切换
    handleSearchTypeChange(value) {
      const selectedType = this.searchTypeOptions.find(item => item.value === value)
      if (selectedType) {
        this.currentSearchType = selectedType
        // 清空输入框
        this.searchForm.orderCodeInput = ''
      }
    },
    // 处理时间类型切换
    handleTimeTypeChange(value) {
      const selectedType = this.timeTypeOptions.find(item => item.value === value)
      if (selectedType) {
        this.currentTimeType = selectedType
        // 清空时间范围
        this.searchForm.timeRange = []
      }
    },
    formatGrabStatus(val) {
      if (val === null || val === undefined || val === '') {
        return '-'
      }
      const n = Number(val)
      if (n === 0) return '未禁止'
      if (n === 1) return '禁止'
      return '-'
    },
    headerCellStyle() {
      return {
        background: '#f7f8fa',
        color: '#303133',
        fontWeight: 600
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.fetchOrderList()
    },
    handleReset() {
      // 重置搜索表单为默认值
      this.searchForm = getDefaultSearchForm()
      // 重置搜索类型为默认值
      this.currentSearchType = { label: '无仓单号', value: 'noWarehouseOrderCode', field: 'noWarehouseOrderCode' }
      // 重置时间类型为默认值
      this.currentTimeType = { label: '创建时间', value: 'createTime', startField: 'createStartTime', endField: 'createEndTime' }
      // 清空批量查询列表
      this.batchOrderCodeList = []
      this.batchMerchantCodeList = []
      this.batchErpOrderCodeList = []
      // 重置分页
      this.pagination.currentPage = 1
      // 重新获取数据
      this.fetchOrderList()
    },
    handleTimeRangeChange(value) {
      if (!value || value.length !== 2) {
        return
      }
      const startDate = new Date(value[0])
      startDate.setHours(0, 0, 0, 0)
      const endDate = new Date(value[1])
      endDate.setHours(0, 0, 0, 0)
      const oneMonthLater = new Date(startDate)
      oneMonthLater.setMonth(oneMonthLater.getMonth() + 1)
      oneMonthLater.setHours(23, 59, 59, 999)
      
      // 如果结束日期超过开始日期后一个月，自动调整为一个月后的日期
      if (endDate.getTime() > oneMonthLater.getTime()) {
        // 格式化为 yyyy-MM-dd
        const formatDate = (date) => {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }
        
        const adjustedEndDate = new Date(oneMonthLater)
        adjustedEndDate.setHours(0, 0, 0, 0)
        const adjustedEndDateStr = formatDate(adjustedEndDate)
        
        this.$message.warning(`${this.currentTimeType.label}查询范围不能超过一个月，已自动调整为一个月范围`)
        this.$nextTick(() => {
          this.searchForm.timeRange = [value[0], adjustedEndDateStr]
        })
      }
    },
    openBatchDialog() {
      this.batchOrderCodeInput = this.batchOrderCodeList.length
        ? this.batchOrderCodeList.join('\n')
        : ''
      this.batchDialogVisible = true
    },
    closeBatchDialog() {
      this.batchDialogVisible = false
      this.batchOrderCodeInput = ''
    },
    openBatchMerchantDialog() {
      this.batchMerchantCodeInput = this.batchMerchantCodeList.length
        ? this.batchMerchantCodeList.join('\n')
        : ''
      this.batchMerchantDialogVisible = true
    },
    closeBatchMerchantDialog() {
      this.batchMerchantDialogVisible = false
      this.batchMerchantCodeInput = ''
    },
    openBatchErpOrderDialog() {
      this.batchErpOrderCodeInput = this.batchErpOrderCodeList.length
        ? this.batchErpOrderCodeList.join('\n')
        : ''
      this.batchErpOrderDialogVisible = true
    },
    closeBatchErpOrderDialog() {
      this.batchErpOrderDialogVisible = false
      this.batchErpOrderCodeInput = ''
    },
    applyBatchErpOrderSearch(val) {
      if (typeof val === 'string') {
        this.batchErpOrderCodeInput = val
      }
      if (!this.batchErpOrderCodeInput.trim()) {
        this.$message.warning('请输入至少一个旺店通单号')
        return
      }
      const codes = this.batchErpOrderCodeInput
        .split(/\n+/)
        .map(code => code.trim())
        .filter(code => !!code)
      if (!codes.length) {
        this.$message.warning('请输入有效的旺店通单号')
        return
      }
      this.batchErpOrderCodeList = Array.from(new Set(codes))
      this.batchErpOrderDialogVisible = false
      this.pagination.currentPage = 1
      this.fetchOrderList()
    },
    applyBatchSearch(val) {
      if (typeof val === 'string') {
        this.batchOrderCodeInput = val
      }
      if (!this.batchOrderCodeInput.trim()) {
        this.$message.warning('请输入至少一个内部单号')
        return
      }
      const orderCodes = this.batchOrderCodeInput
        .split(/\n+/)
        .map(code => code.trim())
        .filter(code => !!code)
      if (!orderCodes.length) {
        this.$message.warning('请输入有效的内部单号')
        return
      }
      this.batchOrderCodeList = Array.from(new Set(orderCodes))
      this.batchDialogVisible = false
      this.pagination.currentPage = 1
      this.fetchOrderList()
    },
    applyBatchMerchantSearch(val) {
      if (typeof val === 'string') {
        this.batchMerchantCodeInput = val
      }
      if (!this.batchMerchantCodeInput.trim()) {
        this.$message.warning('请输入至少一个商家单号')
        return
      }
      const codes = this.batchMerchantCodeInput
        .split(/\n+/)
        .map(code => code.trim())
        .filter(code => !!code)
      if (!codes.length) {
        this.$message.warning('请输入有效的商家单号')
        return
      }
      this.batchMerchantCodeList = Array.from(new Set(codes))
      this.batchMerchantDialogVisible = false
      this.pagination.currentPage = 1
      this.fetchOrderList()
    },
    disabledDate(time) {
      const todayEnd = new Date()
      todayEnd.setHours(23, 59, 59, 999)
      return time.getTime() > todayEnd.getTime()
    },
    // 构建查询参数
    buildQueryParams() {
      const params = {}
      
      /**
       * 单个单号搜索（根据下拉选择的类型）
       */
      if (this.searchForm.orderCodeInput && this.searchForm.orderCodeInput.trim()) {
        const inputValue = this.searchForm.orderCodeInput.trim()
        if (this.currentSearchType.value === 'noWarehouseOrderCode') {
          // 无仓单号
          params.orderCodeList = [inputValue]
        } else if (this.currentSearchType.value === 'originalOrderId') {
          // 商家单号
          params.originalOrderIdList = [inputValue]
        } else if (this.currentSearchType.value === 'erpOrderId') {
          // ERP单号
          params.erpOrderIdList = [inputValue]
        }
      }
      
      /**
       * 批量内部单号查询
       */
      const orderCodeList = this.batchOrderCodeList.length
        ? this.batchOrderCodeList
        : []
      if (orderCodeList.length) {
        params.orderCodeList = orderCodeList
      }

      /**
       * 批量商家单号查询
       */
      const merchantCodeList = this.batchMerchantCodeList.length
        ? this.batchMerchantCodeList
        : []
      if (merchantCodeList.length) {
        params.originalOrderIdList = merchantCodeList
      }

      /**
       * 批量旺店通单号查询
       */
      const erpOrderCodeList = this.batchErpOrderCodeList.length
        ? this.batchErpOrderCodeList
        : []
      if (erpOrderCodeList.length) {
        params.erpOrderIdList = erpOrderCodeList
      }

      /**
       * 时间范围查询（根据下拉选择的时间类型）
       */
      if (this.searchForm.timeRange && this.searchForm.timeRange.length === 2) {
        const startTime = `${this.searchForm.timeRange[0]} 00:00:00`
        const endTime = `${this.searchForm.timeRange[1]} 23:59:59`
        
        // 根据当前选择的时间类型设置对应的参数
        params[this.currentTimeType.startField] = startTime
        params[this.currentTimeType.endField] = endTime
      }

      return params
    },
    async fetchOrderList() {
      this.loading = true
      try {
        const pageData = {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const params = this.buildQueryParams()
        const res = await getAllOrderListApi(pageData, params)
        if (res && res.code === 200) {
          this.tableData = res.rows || []
          this.pagination.total = res.total || 0
        } else {
          this.tableData = []
          this.pagination.total = 0
          this.$message.error(res?.msg || '获取订单列表失败')
        }
      } catch (error) {
        console.error('获取订单列表失败', error)
        this.tableData = []
        this.pagination.total = 0
        this.$message.error('获取订单列表失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(size) {
      this.pagination.pageSize = size
      this.pagination.currentPage = 1
      this.fetchOrderList()
    },
    handleCurrentChange(page) {
      this.pagination.currentPage = page
      this.fetchOrderList()
    },
    getStatusTagType(status) {
      const map = {
        1: 'info',
        2: 'info',
        3: 'warning',
        4: 'warning',
        5: 'success',
        6: 'primary',
        7: 'warning',
        8: 'danger',
        9: 'info',
        10: 'success',
        11: 'danger',
        12: 'danger'
      }
      return map[status] || 'info'
    },
    getOrderType(orderType){
      const map = {
        1: '入仓',
        2: '代发'
      }
      return map[orderType] || '代发'
    },
    getStatusText(status) {
      const map = {
        1: '新建中',
        2: '待发布',
        3: '报价中',
        4: '发货中',
        5: '当日发货',
        6: '在途',
        7: '追单',
        8: '异常订单',
        9: '待确定收货',
        10: '已完成',
        11: '撤销',
        12: '售后'
      }
      return map[status] || '未知状态'
    },
    handleCopyAddress(row) {
      const addressText = `收件人:${row.addressee || ''}，电话:${row.phone || ''}，地址:${row.receivingAddress || ''}`
      this.copyText(addressText)
    },
    handleViewSerial(row) {
      this.currentOrder = row
      this.imeiDialogVisible = true
    },
    handleViewLogistics(row) {
      this.currentOrder = row
      this.logisticsDialogVisible = true
    },
    handleBillDetails(row) {
      this.$message.info(`账单详情开发中：${row.orderCode || '-'}`)
    },
    handleGenerateExcel() {
      this.$message.info('生成Excel功能开发中，敬请期待')
    },
    handleDownloadExcel() {
      this.$message.info('请先生成Excel后再下载')
    },
    async handleExport() {
      this.exportLoading = true
      try {
        const params = this.buildQueryParams()
        const res = await getAllOrderExportApi(params)
        const isBlob = blobValidate(res)
        if (isBlob) {
          const blob = new Blob([res])
          const now = new Date()
          const timestamp =
            now.getFullYear() +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            '_' +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0')
          const fileName = `全部订单_${timestamp}.xlsx`
          saveAs(blob, fileName)
          this.$message.success('导出成功')
        } else {
          const reader = new FileReader()
          reader.onload = (e) => {
            try {
              const result = JSON.parse(e.target.result || '{}')
              this.$message.error(result.msg || '导出失败')
            } catch (error) {
              this.$message.error('导出失败')
            }
          }
          reader.readAsText(res)
        }
      } catch (error) {
        console.error('导出全部订单失败', error)
        this.$message.error('导出失败，请稍后重试')
      } finally {
        this.exportLoading = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/assets/styles/common/order-components.scss";
.all-orders-page {
  padding: 12px;
  display: flex;
  flex-direction: column;
  min-height: calc(100vh - 100px);
  box-sizing: border-box;
}

.search-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(15, 35, 95, 0.08);
  border: 1px solid #eef2ff;
  margin-bottom: 12px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #4b8dff, #55d3ff);
  }
}

.search-content {
  display: flex;
  align-items: flex-end;
}

.search-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex: 1;
  gap: 12px;
}

.search-row {
  display: flex;
  align-items: flex-end;
  width: 100%;
  gap: 16px;
}

.search-item {
  flex: 0 0 40%;
  max-width: 40%;
  display: flex;
  align-items: center;
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  min-width: fit-content;
  flex-shrink: 0;
}

.search-label::before {
  content: "";
  width: 4px;
  height: 4px;
  background: #409eff;
  border-radius: 50%;
}

.search-label-dropdown {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.search-label-select {
  font-size: 14px;
  font-weight: 500;
  color: #409eff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  padding: 0 12px;
  height: 36px;
  border-radius: 4px 0 0 4px;
  user-select: none;
  background: linear-gradient(135deg, #ecf5ff 0%, #f0f9ff 100%);
  border: 1px solid #DCDFE6;
  position: relative;
  border-right: none;
  box-sizing: border-box;
  
  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
  }
  
  .el-icon--right {
    margin-left: 4px;
    font-size: 12px;
    font-weight: bold;
    transition: transform 0.3s ease;
    
    &.is-rotate {
      transform: rotate(180deg);
    }
  }
}

.search-label-dropdown :deep(.el-dropdown-menu) {
  border: 2px solid #b3d8ff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
  border-radius: 8px;
  padding: 4px;
  margin-top: 4px;
}

.search-label-dropdown :deep(.el-dropdown-menu__item) {
  font-size: 14px;
  padding: 10px 16px;
  border-radius: 6px;
  margin: 2px 0;
  transition: all 0.2s ease;
  
  &:hover {
    background: #f0f9ff;
    color: #409eff;
    transform: translateX(4px);
  }
  
  &.is-active {
    color: #fff;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    
    &::before {
      content: "✓";
      margin-right: 8px;
      font-weight: bold;
    }
  }
}

.search-input {
  flex: 1;
  min-width: 0;
  width: 100%;
}

.search-input :deep(.el-input__inner),
.search-input :deep(.el-range-input) {
  border-radius: 8px;
  font-size: 14px;
  padding: 6px 10px;
  transition: all 0.3s ease;
  border: 2px solid #e4e7ed;
  background: #fff;
}

.search-input :deep(.el-input__inner:focus),
.search-input :deep(.el-range-editor.is-active) {
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
  background: #fafbfc;
}

.search-actions {
  display: flex;
  flex-direction: column;
  width: 220px;
  justify-content: space-between;
  align-items: flex-end;
  gap: 6px;
}

.primary-actions {
  display: flex;
  gap: 8px;
}

.secondary-actions {
  display: flex;
  justify-content: flex-end;
  width: 100%;
  padding-top: 8px;
  margin-top: 4px;
  border-top: 1px solid #ebeef5;
}

.search-action-btn {
  min-height: 32px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.reset-btn,
.batch-btn,
.export-btn {
  min-height: 32px;
  font-size: 14px;
  border-radius: 8px;
}

.advanced-btn {
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 4px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px 8px;

  .data-tip {
    color: #606266;
    font-size: 14px;

    strong {
      color: #303133;
      margin-left: 4px;
    }
  }
}

.table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 35, 95, 0.08);
  border: 1px solid #eef2ff;
  overflow: hidden;
  min-height: 0;
}

.pagination-wrapper {
  margin-top: 16px;
  text-align: right;
}

.order-product {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #303133;
}

.order-product-line {
  line-height: 20px;
}

.supplier-name {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-weight: 500;
}

.supplier-line {
  font-size: 13px;
  color: #303133;
  line-height: 24px;
}

.time-line {
  font-size: 13px;
  color: #303133;
  line-height: 20px;
}

.sub-status {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}

.batch-tips {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  span {
    background: #f4f6fb;
    padding: 2px 8px;
    border-radius: 4px;
    color: #606266;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1200px) {
  .search-card :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }
}
</style>
