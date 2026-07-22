<!--退货追单-->
<template>
  <div class="order-detail refund-order">
    <!-- 页面 Banner -->
    <div class="page-banner">
      <div class="banner-inner">
        <div class="banner-top">
          <div class="banner-info">
            <h2 class="banner-title">
              <i class="el-icon-s-order" />
              退货追单管理
            </h2>
            <p class="banner-desc">管理退货追单订单，处理发货流程</p>
          </div>
          <div class="banner-actions">
            <el-button class="banner-btn" icon="el-icon-refresh" @click="fetchOrderList">刷新</el-button>
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
              <EmptyState text="暂无退货追单数据" />
            </template>
            <el-table-column prop="orderStyle" label="订单类型" width="85" fixed="left" align="center">
              <template slot-scope="scope">
                <OrderStyleBadge :order-style="scope.row.orderStyle" />
              </template>
            </el-table-column>
            <el-table-column label="单号" prop="orderCode" min-width="180" fixed="left" align="center">
              <template slot-scope="scope">
                <div class="order-numbers"><div class="order-number-item">{{ scope.row.orderCode || '-' }}<i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon" @click="copyText(scope.row.orderCode)" /></div></div>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="subStatus" width="130" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.subStatus)" size="small" effect="light" class="status-tag">{{ formatSubStatus(scope.row.subStatus) }}</el-tag>
              </template>
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
            <el-table-column prop="sendTime" label="发货时间" width="180" align="center"><template slot-scope="scope">{{ scope.row.sendTime || '-' }}</template></el-table-column>
            <el-table-column prop="trackingCompany" label="物流信息" width="200" align="center">
              <template slot-scope="scope"><TrackingInfo :company="scope.row.trackingCompany" :number="scope.row.trackingNumber" :data="scope.row" @click="handleLogisticsInfo" /></template>
            </el-table-column>
            <el-table-column prop="confirmTime" label="确认追单时间" width="180" align="center"><template slot-scope="scope">{{ scope.row.confirmTime || '-' }}</template></el-table-column>
            <el-table-column label="操作" width="260" fixed="right" align="center">
              <template slot-scope="scope">
                <div class="operation-buttons">
                  <el-button size="small" class="op-btn" @click="handleImeiInfo(scope.row)">串码信息</el-button>
                  <el-button v-if="scope.row.subStatus !== 71 && scope.row.subStatus !== 72" size="small" type="primary" class="op-btn" @click="handleOperation(scope.row)">确认追单</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination :current-page="pagination.current" :page-sizes="[30, 50, 100]" :page-size="pagination.size" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" background class="custom-pagination" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
          </div>
        </div>
      </div>
    </div>

    <SerialAdjustmentDialog :visible.sync="serialAdjustmentDialogVisible" @confirm="handleConfirmSerialAdjustment" @cancel="handleCancelSerialAdjustment" @close="handleCancelSerialAdjustment" />
    <ImeiDialog :visible.sync="imeiDialogVisible" :current-order="imeiDialogData" @confirm="handleConfirmImei" @cancel="handleCancelImei" @close="handleCancelImei" />
    <LogisticsDialog :visible.sync="logisticsDialogVisible" :current-order="logisticsDialogData" @confirm="handleConfirmLogistics" @cancel="handleCancelLogistics" @close="handleCancelLogistics" />
  </div>
</template>

<script>
import SerialAdjustmentDialog from '@/components/SerialAdjustmentDialog'
import EmptyState from '@/components/EmptyState'
import ImeiDialog from '@/components/ImeiDialog'
import LogisticsDialog from '@/components/LogisticsDialog'
import RegionSidebar from '@/components/RegionSidebar'
import OrderSearch from '@/components/OrderSearch'
import TrackingInfo from '@/components/TrackingInfo'
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getChaseList, confirmChase } from '@/api/order'
import { getDeliveryTimeText } from '@/utils/deliveryTime'

export default {
  name: 'TodayOrder',
  components: { SerialAdjustmentDialog, EmptyState, ImeiDialog, LogisticsDialog, RegionSidebar, OrderSearch, TrackingInfo, OrderStyleBadge },
  data() {
    return {
      loading: false, orderList: [], selectedRegion: '', selectedBrand: '', currentStatus: 7,
      searchForm: { orderCode: '', productName: '', skuName: '' },
      pagination: { current: 1, size: 30, total: 0 },
      serialAdjustmentDialogVisible: false,
      logisticsDialogVisible: false, logisticsDialogData: {},
      imeiDialogVisible: false, imeiDialogData: {}
    }
  },
  computed: {},
  mounted() { this.fetchOrderList() },
  methods: {
    getDeliveryTimeText,
    handleSearch(searchData) { this.searchForm = { ...searchData }; this.pagination.current = 1; this.fetchOrderList() },
    handleReset(searchData) { this.searchForm = { ...searchData }; this.pagination.current = 1; this.fetchOrderList() },
    async fetchOrderList() {
      this.loading = true
      const res = await getChaseList({ province: this.selectedRegion, brand: this.selectedBrand, orderCode: this.searchForm.orderCode, productName: this.searchForm.productName, skuName: this.searchForm.skuName }, { pageNum: this.pagination.current, pageSize: this.pagination.size })
      if (res && res.code === 200) { this.orderList = res.rows; this.pagination.total = res.total } else { this.$message.error(res?.msg || '获取订单列表失败') }
      this.loading = false
    },
    handleSizeChange(size) { this.pagination.size = size; this.fetchOrderList() },
    handleCurrentChange(current) { this.pagination.current = current; this.fetchOrderList() },
    handleGrab(row) { this.$message.success(`抢单成功：${row.orderNo}`) },
    handleView(row) { this.$message.info(`查看订单：${row.orderNo}`) },
    handleSerialAdjustment() { this.serialAdjustmentDialogVisible = true },
    handleConfirmSerialAdjustment(data) { this.$message.success('串码调整申请已提交'); this.serialAdjustmentDialogVisible = false },
    handleCancelSerialAdjustment() { this.serialAdjustmentDialogVisible = false },
    handleImeiInfo(row) { this.imeiDialogData = { ...row }; this.imeiDialogVisible = true },
    handleConfirmImei() { this.imeiDialogVisible = false },
    handleCancelImei() { this.imeiDialogVisible = false },
    handleLogisticsInfo(row) { this.logisticsDialogData = { ...row }; this.logisticsDialogVisible = true },
    handleConfirmLogistics() { this.logisticsDialogVisible = false },
    handleCancelLogistics() { this.logisticsDialogVisible = false },
    handleOperation(row) {
      this.$confirm('确认要追单此订单吗？', '确认追单', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
        .then(async() => { const res = await confirmChase(row.orderCode); if (res && res.code === 200) { this.$message.success('确认追单成功'); this.fetchOrderList() } else { this.$message.error(res?.msg || '确认追单失败，请稍后重试') } })
        .catch(() => { this.$message.info('已取消追单') })
    },
    handleRegionChange(regionData) { this.selectedRegion = regionData; this.pagination.current = 1; this.fetchOrderList() },
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
    formatRevokeReason(revokeType) {
      if (revokeType === null || revokeType === undefined || revokeType === '') return '-'
      return { 0: '店铺后台急速退款', 1: '顾客拒签/拒收', 2: '派送未联系到顾客,退回', 3: '24小时物流无揽收信息', 4: '供应商私自拦截', 5: '已经从其他渠道发货', 6: '手动追单' }[revokeType] || `未知原因(${revokeType})`
    },
    formatSubStatus(subStatus) {
      if (subStatus === null || subStatus === undefined || subStatus === '') return '-'
      return { 71: '未发货待确认', 72: '已发货待确认', 73: '未发货已确认', 74: '已发货已确认' }[subStatus] || `未知状态(${subStatus})`
    },
    getStatusType(subStatus) {
      return { 71: 'warning', 72: 'primary', 73: 'success', 74: 'info' }[subStatus] || 'info'
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/order-modern.scss';
</style>
