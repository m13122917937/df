<!--售后-->
<template>
  <div class="order-detail after-sales-order">
    <div class="page-banner">
      <div class="banner-inner">
        <div class="banner-top">
          <div class="banner-info">
            <h2 class="banner-title"><i class="el-icon-s-order" />售后订单管理</h2>
            <p class="banner-desc">管理售后订单，处理售后流程</p>
          </div>
        </div>
      </div>
    </div>

    <div class="order-body">
      <div class="filter-sidebar">
        <div class="sidebar-header"><i class="el-icon-location-outline" />收货地点</div>
        <RegionSidebar :status="currentStatus" :current-value="selectedRegion" :current-brand="selectedBrand" @change="handleRegionChange" />
      </div>
      <div class="order-main">
        <div class="search-card">
          <OrderSearch :value="searchForm" @search="handleSearch" @reset="handleReset" />
        </div>
        <div class="table-card">
          <el-table ref="table" v-loading="loading" :data="orderList" stripe size="medium" style="width: 100%" :fit="true" height="100%">
            <template slot="empty"><EmptyState text="暂无售后数据" /></template>
            <el-table-column prop="orderStyle" label="订单类型" width="85" fixed="left" align="center">
              <template slot-scope="scope"><OrderStyleBadge :order-style="scope.row.orderStyle" /></template>
            </el-table-column>
            <el-table-column label="单号" prop="orderCode" min-width="180" fixed="left" align="center">
              <template slot-scope="scope">
                <div class="order-numbers"><div class="order-number-item">{{ scope.row.orderCode || '-' }}<i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon" @click="copyText(scope.row.orderCode)" /></div></div>
              </template>
            </el-table-column>
            <el-table-column prop="deliveryTime" label="发货时效" width="110" align="center"><template slot-scope="scope">{{ getDeliveryTimeText(scope.row.deliveryDeadline) }}</template></el-table-column>
            <el-table-column prop="subStatus" label="发货状态" width="120" align="center">
              <template slot-scope="scope"><span style="color: #FF3B30; font-weight: 500;">{{ getsubStatusText(scope.row.subStatus) }}</span></template>
            </el-table-column>
            <el-table-column prop="brand" label="品牌/品类" width="110" align="center">
              <template slot-scope="scope">
                <div class="order-productName"><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.brand || '-' }}</span></div><div class="order-productName-line"><span v-if="scope.row.category" class="pn-model">{{ scope.row.category }}</span></div></div>
              </template>
            </el-table-column>
            <el-table-column label="产品型号" prop="productName" min-width="180" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <div class="order-productName"><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.productName || '-' }}</span></div><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.skuName }}</span></div></div>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" align="center" sortable><template slot-scope="scope">{{ scope.row.quantity || 0 }}</template></el-table-column>
            <el-table-column label="成交价" min-width="110" align="center"><template slot-scope="scope"><span class="price-text">{{ scope.row.tradePrice || '-' }}</span></template></el-table-column>
            <el-table-column label="账期" prop="accountingPeriod" min-width="110" align="center"><template slot-scope="scope"><span>{{ formatAccountingPeriod(scope.row.accountingPeriod) }}</span></template></el-table-column>
            <el-table-column label="收货地" prop="address" min-width="180" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <el-popover placement="top" trigger="hover" popper-class="addr-popover" :open-delay="150" :close-delay="100">
                  <div class="addr-content"><div class="addr-line main">收件人: {{ scope.row.addressee || '无' }}</div><div class="addr-line sub">电话: {{ scope.row.phone || '无' }}</div><div class="addr-line sub">地址: {{ scope.row.receivingAddress || '无' }}<i v-if="scope.row.receivingAddress" class="el-icon-copy-document copy-icon" @click="copyText(`收件人:${scope.row.addressee};电话:${scope.row.phone};地址:${scope.row.receivingAddress}`)" /></div></div>
                  <span slot="reference">{{ scope.row.provinceName }} {{ scope.row.cityName }}</span>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="tradeTime" label="抢单时间" width="180" align="center"><template slot-scope="scope">{{ scope.row.tradeTime || '-' }}</template></el-table-column>
            <el-table-column prop="tradeUserName" label="抢单人" width="100" align="center"><template slot-scope="scope">{{ scope.row.tradeUserName || '-' }}</template></el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination :current-page="pagination.current" :page-sizes="[30, 50, 100]" :page-size="pagination.size" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" background class="custom-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
          </div>
        </div>
      </div>
    </div>

    <ShippingRulesDialog :visible.sync="shippingRulesDialogVisible" @close="handleCloseShippingRules" />
    <ImportShippingDialog :visible.sync="importShippingDialogVisible" :bulk-shipping-data="bulkShippingList" :failed-orders-count="failedOrdersCount" @export-pending-orders="handleExportPendingOrders" @import-shipping-info="handleImportShippingInfo" @refresh-import-status="handleRefreshImportStatus" @close="handleCloseImportShipping" />
  </div>
</template>

<script>
import ShippingRulesDialog from '@/components/ShippingRulesDialog'
import ImportShippingDialog from '@/components/ImportShippingDialog'
import EmptyState from '@/components/EmptyState'
import RegionSidebar from '@/components/RegionSidebar'
import OrderSearch from '@/components/OrderSearch'
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getAfterSalesList } from '@/api/order'
import { getDeliveryTimeText } from '@/utils/deliveryTime'

export default {
  name: 'TodayOrder',
  components: { ShippingRulesDialog, ImportShippingDialog, EmptyState, RegionSidebar, OrderSearch, OrderStyleBadge },
  data() {
    return {
      loading: false, orderList: [], pagination: { current: 1, size: 30, total: 0 },
      selectedRegion: '', selectedBrand: '', currentStatus: 12,
      searchForm: { orderCode: '', productName: '', skuName: '' },
      shippingRulesDialogVisible: false,
      importShippingDialogVisible: false, bulkShippingList: [], failedOrdersCount: 0
    }
  },
  computed: {},
  mounted() { this.fetchOrderList() },
  methods: {
    getDeliveryTimeText,
    getsubStatusText(subStatus) {
      const map = { 41: '待填写串码', 42: '待填写物流信息', 43: '平台二销验证中' }
      return map[subStatus] || '-'
    },
    handleRegionChange(region) { this.selectedRegion = region; this.pagination.current = 1; this.fetchOrderList() },
    handleSearch(searchData) { this.searchForm = { ...searchData }; this.pagination.current = 1; this.fetchOrderList() },
    handleReset(searchData) { this.searchForm = { ...searchData }; this.pagination.current = 1; this.fetchOrderList() },
    async fetchOrderList() {
      this.loading = true
      const res = await getAfterSalesList({ province: this.selectedRegion, brand: this.selectedBrand, orderCode: this.searchForm.orderCode, productName: this.searchForm.productName, skuName: this.searchForm.skuName }, { pageNum: this.pagination.current, pageSize: this.pagination.size })
      if (res && res.code === 200) { this.orderList = res.rows; this.pagination.total = res.total } else { this.$message.error(res?.msg || '获取订单列表失败') }
      this.loading = false
    },
    handleSizeChange(size) { this.pagination.size = size; this.fetchOrderList() },
    handleCurrentChange(current) { this.pagination.current = current; this.fetchOrderList() },
    handleView(row) { this.$message.info(`查看订单：${row.orderNo}`) },
    getCodeOptionType(codeOptions) { return { 0: 'success', 1: 'warning', 2: 'info' }[codeOptions] || 'info' },
    getCodeOptionText(codeOptions) { return { 0: '发货前提供', 1: '发货后提供', 2: '不需要' }[codeOptions] || '未知' },
    formatAccountingPeriod(val) { const n = Number(val); if (!isFinite(n) || n < 0) return '-'; return n === 0 ? '当天' : `T+${n}` },
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) { navigator.clipboard.writeText(text).then(() => this.$message.success('复制成功')).catch(() => this.fallbackCopyText(text)) } else { this.fallbackCopyText(text) }
    },
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea'); textArea.value = text; textArea.style.position = 'fixed'; textArea.style.left = '-999999px'; textArea.style.top = '-999999px'; document.body.appendChild(textArea); textArea.focus(); textArea.select()
      try { document.execCommand('copy'); this.$message.success('复制成功') } catch (err) { this.$message.error('复制失败') }
      document.body.removeChild(textArea)
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
