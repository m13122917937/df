<!--待发货-->
<template>
  <div class="order-detail pending-order">
    <!-- 页面 Banner -->
    <div class="page-banner">
      <div class="banner-inner">
        <div class="banner-top">
          <div class="banner-info">
            <h2 class="banner-title">
              <i class="el-icon-s-order" />
              待发货订单管理
              <span class="banner-warning">每周日查出二销的订单只能免责毁单，请各位老板及时关注订单情况</span>
            </h2>
            <p class="banner-desc">管理待发货订单，处理发货流程</p>
          </div>
          <div class="banner-actions">
            <el-button class="banner-btn" icon="el-icon-download" :loading="exportLoading" @click="handleExportOrder">导出待发货订单</el-button>
            <el-button class="banner-btn" icon="el-icon-upload2" @click="handleImportOrder">批量导入发货信息</el-button>
            <el-button class="banner-btn" icon="el-icon-document" @click="handleAddOrder">发货规则</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="order-body">
      <!-- 左侧地区筛选 -->
      <div class="filter-sidebar">
        <div class="sidebar-header">
          <i class="el-icon-location-outline" />
          <span>收货地点</span>
        </div>
        <RegionSidebar
          ref="regionSidebar"
          :status="currentStatus"
          :current-value="selectedRegion"
          :current-brand="selectedBrand"
          @change="handleRegionChange"
        />
      </div>

      <!-- 右侧主区域 -->
      <div class="order-main">
        <!-- 搜索卡片 -->
        <div class="search-card">
          <OrderSearch
            :value="searchForm"
            @search="handleSearch"
            @reset="handleReset"
          />
        </div>

        <!-- 表格卡片 -->
        <div class="table-card">
          <el-table
            ref="table"
            v-loading="loading"
            :data="orderList"
            stripe
            size="medium"
            style="width: 100%"
            :fit="true"
            height="100%"
          >
            <template slot="empty">
              <EmptyState text="暂无待发货数据" />
            </template>
            <el-table-column prop="orderType" label="业务类型" width="80" fixed="left" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.orderType === 1 ? 'success' : 'warning'" size="mini">
                  {{ scope.row.orderType === 1 ? '入仓' : '代发' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="orderStyle" label="订单类型" width="85" fixed="left" align="center">
              <template slot-scope="scope">
                <OrderStyleBadge :order-style="scope.row.orderStyle" />
              </template>
            </el-table-column>
            <el-table-column label="单号" prop="orderCode" min-width="180" fixed="left" align="center">
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">
                    {{ scope.row.orderCode || '-' }}
                    <i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon" @click="copyText(scope.row.orderCode)" />
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="deliveryTime" label="发货时效" width="110" align="center">
              <template slot-scope="scope">
                {{ getDeliveryTimeText(scope.row.deliveryDeadline) }}
              </template>
            </el-table-column>
            <el-table-column prop="subStatus" label="发货状态" width="180" align="center">
              <template slot-scope="scope">
                <div class="shipping-status-container">
                  <div class="status-item">
                    <span class="status-label">串码状态：</span>
                    <span class="status-value">{{ getSerialCodeStatusText(scope.row.subStatus) }}</span>
                  </div>
                  <div class="status-item">
                    <span class="status-label">物流单号：</span>
                    <span class="status-value">{{ getTrackingNumberStatusText(scope.row.trackingNumber) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="brand" label="品牌/品类" width="110" align="center">
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{ scope.row.brand || '-' }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.category" class="pn-model">{{ scope.row.category }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="产品型号" prop="productName" min-width="180" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{ scope.row.productName || '-' }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{ scope.row.skuName }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" align="center" sortable>
              <template slot-scope="scope">{{ scope.row.quantity || 0 }}</template>
            </el-table-column>
            <el-table-column label="成交价" min-width="110" align="center">
              <template slot-scope="scope">
                <span class="price-text">{{ scope.row.tradePrice || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="账期" prop="accountingPeriod" min-width="110" align="center">
              <template slot-scope="scope">
                <span>{{ formatAccountingPeriod(scope.row.accountingPeriod) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="收货地" prop="address" min-width="180" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <el-popover placement="top" trigger="hover" popper-class="addr-popover" :open-delay="150" :close-delay="100">
                  <div class="addr-content">
                    <div class="addr-line main">收件人: {{ scope.row.addressee || '无' }}</div>
                    <div class="addr-line sub">电话: {{ scope.row.phone || '无' }}</div>
                    <div class="addr-line sub">
                      地址: {{ scope.row.receivingAddress || '无' }}
                      <i v-if="scope.row.receivingAddress" class="el-icon-copy-document copy-icon" @click="copyText(`收件人:${scope.row.addressee};电话:${scope.row.phone};地址:${scope.row.receivingAddress}`)" />
                    </div>
                  </div>
                  <span slot="reference">{{ scope.row.provinceName }} {{ scope.row.cityName }}</span>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="tradeTime" label="抢单时间" width="180" align="center">
              <template slot-scope="scope">{{ scope.row.tradeTime || '-' }}</template>
            </el-table-column>
            <el-table-column prop="tradeUserName" label="抢单人" width="100" align="center">
              <template slot-scope="scope">{{ scope.row.tradeUserName || '-' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="260" fixed="right" align="center">
              <template slot-scope="scope">
                <div class="operation-buttons">
                  <el-button size="small" class="op-btn" @click="handleSubmitSerial(scope.row)">操作</el-button>
                  <el-button v-if="scope.row.handleApply == 0" size="small" class="op-btn" @click="handleCancelDetail(scope.row)">毁单详情</el-button>
                  <el-button v-else size="small" class="op-btn el-button--warning" @click="handleCancelOrder(scope.row)">毁单</el-button>
                </div>
                <div class="status-message">*串码验证通过且运单号已保存的将自动完成发货</div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              :current-page="pagination.current"
              :page-sizes="[30, 50, 100]"
              :page-size="pagination.size"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              background
              class="custom-pagination"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 提交串码弹窗 -->
    <SubmitSerialDialog
      :visible.sync="submitSerialDialogVisible"
      :order-data="currentOrder"
      @save="handleSaveSubmitSerial"
      @cancel="handleCancelSubmitSerial"
      @close="handleCancelSubmitSerial"
    />
    <!-- 毁单弹窗 -->
    <CancelOrderDialog
      :visible.sync="cancelOrderDialogVisible"
      :order-data="cancelOrderData"
      @confirm="handleConfirmCancelOrder"
      @cancel="handleCancelCancelOrder"
      @close="handleCancelCancelOrder"
    />
    <!-- 毁单详情弹窗 -->
    <CancelOrderDetailDialog
      :visible.sync="cancelDetailDialogVisible"
      :order-data="cancelDetailData"
      @close="cancelDetailDialogVisible = false"
    />
    <!-- 发货规则弹窗 -->
    <ShippingRulesDialog
      :visible.sync="shippingRulesDialogVisible"
      @close="handleCloseShippingRules"
    />
    <!-- 导入发货信息弹窗 -->
    <ImportShippingDialog
      :visible.sync="importShippingDialogVisible"
      :bulk-shipping-data="bulkShippingList"
      :failed-orders-count="failedOrdersCount"
      @export-pending-orders="handleExportPendingOrders"
      @import-shipping-info="handleImportShippingInfo"
      @refresh-import-status="handleRefreshImportStatus"
      @refresh-table="fetchOrderList"
      @close="handleCloseImportShipping"
    />
  </div>
</template>

<script>
import ShippingRulesDialog from '@/components/ShippingRulesDialog'
import SubmitSerialDialog from '@/components/SubmitSerialDialog'
import CancelOrderDialog from '@/components/CancelOrderDialog'
import ImportShippingDialog from '@/components/ImportShippingDialog'
import CancelOrderDetailDialog from '@/components/CancelOrderDetailDialog'
import EmptyState from '@/components/EmptyState'
import RegionSidebar from '@/components/RegionSidebar'
import OrderSearch from '@/components/OrderSearch'
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getIngList, exportOrder } from '@/api/order'
import { getDeliveryTimeText } from '@/utils/deliveryTime'

export default {
  name: 'TodayOrder',
  components: {
    ShippingRulesDialog,
    SubmitSerialDialog,
    CancelOrderDialog,
    ImportShippingDialog,
    CancelOrderDetailDialog,
    EmptyState,
    RegionSidebar,
    OrderSearch,
    OrderStyleBadge
  },
  data() {
    return {
      loading: false,
      orderList: [],
      pagination: { current: 1, size: 30, total: 0 },
      selectedRegion: '',
      selectedBrand: '',
      currentStatus: 4,
      searchForm: { orderCode: '', productName: '', skuName: '' },
      submitSerialDialogVisible: false,
      currentOrder: {},
      cancelOrderDialogVisible: false,
      cancelOrderData: {},
      cancelDetailDialogVisible: false,
      cancelDetailData: {},
      shippingRulesDialogVisible: false,
      importShippingDialogVisible: false,
      bulkShippingList: [],
      failedOrdersCount: 0,
      exportLoading: false
    }
  },
  computed: {
  },
  mounted() {
    this.fetchOrderList()
  },
  methods: {
    getDeliveryTimeText,
    getSerialCodeStatusText(subStatus) {
      const map = { 41: '待填写串码', 42: '平台二销验证中', 43: '平台二销验证中', 44: '平台已销售' }
      return map[subStatus] || '-'
    },
    getTrackingNumberStatusText(trackingNumber) {
      return trackingNumber || '待上传物流单号'
    },
    handleRegionChange(region) {
      this.selectedRegion = region
      this.pagination.current = 1
      this.fetchOrderList()
    },
    handleSearch(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.current = 1
      this.fetchOrderList()
    },
    handleReset(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.current = 1
      this.fetchOrderList()
    },
    async fetchOrderList() {
      this.loading = true
      const res = await getIngList({
        province: this.selectedRegion, brand: this.selectedBrand,
        orderCode: this.searchForm.orderCode, productName: this.searchForm.productName, skuName: this.searchForm.skuName
      }, { pageNum: this.pagination.current, pageSize: this.pagination.size })
      if (res && res.code === 200) {
        this.orderList = res.rows
        this.pagination.total = res.total
      } else {
        this.$message.error(res?.msg || '获取订单列表失败')
      }
      this.loading = false
    },
    handleSizeChange(size) { this.pagination.size = size; this.fetchOrderList() },
    handleCurrentChange(current) { this.pagination.current = current; this.fetchOrderList() },
    handleView(row) { this.$message.info(`查看订单：${row.orderNo}`) },
    handleTagFilterConfirm(selectedObjects) { this.$message.success('标签筛选已应用') },
    handleTagFilterCancel() {},
    handleOrderNumberFilterConfirm(selectedObjects) { this.$message.success('订单编号搜索已应用') },
    handleOrderNumberFilterCancel() {},
    handleShippingTimeFilterConfirm(selectedObjects) { this.$message.success('发货时效筛选已应用') },
    handleShippingTimeFilterCancel() {},
    handleArrivalTimeFilterConfirm(selectedObjects) { this.$message.success('到货时效筛选已应用') },
    handleArrivalTimeFilterCancel() {},
    handleProductFilterConfirm(selectedObjects) { this.$message.success('产品型号筛选已应用') },
    handleProductFilterCancel() {},
    getCodeOptionType(codeOptions) { return { 0: 'success', 1: 'warning', 2: 'info' }[codeOptions] || 'info' },
    getCodeOptionText(codeOptions) { return { 0: '发货前提供', 1: '发货后提供', 2: '不需要' }[codeOptions] || '未知' },
    formatAccountingPeriod(val) {
      const n = Number(val)
      if (!isFinite(n) || n < 0) return '-'
      return n === 0 ? '当天' : `T+${n}`
    },
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(() => this.$message.success('复制成功')).catch(() => this.fallbackCopyText(text))
      } else { this.fallbackCopyText(text) }
    },
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      try { document.execCommand('copy'); this.$message.success('复制成功') } catch (err) { this.$message.error('复制失败') }
      document.body.removeChild(textArea)
    },
    handleSubmitSerial(row) { this.currentOrder = row; this.submitSerialDialogVisible = true },
    handleCancelDetail(row) { this.cancelDetailData = { ...row }; this.cancelDetailDialogVisible = true },
    handleCancelOrder(row) { this.cancelOrderData = { ...row }; this.cancelOrderDialogVisible = true },
    handleCancelSubmitSerial() { this.submitSerialDialogVisible = false },
    handleSaveSubmitSerial(data) {
      this.$message.success('串码提交成功')
      this.submitSerialDialogVisible = false
      this.fetchOrderList()
    },
    handleConfirmCancelOrder() {
      this.fetchOrderList()
      if (this.$refs.regionSidebar && this.$refs.regionSidebar.getProvinceData) { this.$refs.regionSidebar.getProvinceData() }
      this.cancelOrderDialogVisible = false
    },
    handleCancelCancelOrder() { this.cancelOrderDialogVisible = false },
    handleAddOrder() { this.shippingRulesDialogVisible = true },
    handleImportOrder() { this.importShippingDialogVisible = true },
    async handleExportOrder() {
      try {
        this.exportLoading = true
        const response = await exportOrder({
          province: this.selectedRegion, brand: this.selectedBrand,
          orderCode: this.searchForm.orderCode, productName: this.searchForm.productName, skuName: this.searchForm.skuName
        }, { pageNum: this.pagination.current, pageSize: this.pagination.size })
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        const now = new Date()
        const ts = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}_${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}`
        link.download = `待发货订单_${ts}.xlsx`
        document.body.appendChild(link); link.click(); document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败，请稍后重试')
      } finally { this.exportLoading = false }
    },
    handleCloseShippingRules() { this.shippingRulesDialogVisible = false },
    handleExportPendingOrders() { this.$message.success('导出待发货订单功能') },
    handleImportShippingInfo() { this.$message.success('导入发货信息功能') },
    handleRefreshImportStatus() { this.$message.info('刷新导入状态') },
    handleCloseImportShipping() { this.importShippingDialogVisible = false }
  }
}
</script>

<style lang="scss">
@import '@/styles/order-modern.scss';
</style>

<style lang="scss" scoped>
// Page-specific styles
.banner-warning {
  margin-left: 16px;
  background: rgba(255, 107, 53, 0.08);
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  color: #FF6B35;
  font-weight: 500;
  border: 1px solid rgba(255, 107, 53, 0.15);
  white-space: nowrap;
}

.shipping-status-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: left;
  padding: 2px 0;
  line-height: 1.4;
  .status-item {
    font-size: 12px;
    .status-label { color: #909399; }
    .status-value { color: #FF3B30; font-weight: 500; }
  }
}

.el-button--warning.op-btn {
  background: #FF9500;
  border-color: #FF9500;
  color: #fff;
  &:hover {
    background: #e68600;
    border-color: #e68600;
  }
}
</style>
