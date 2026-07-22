<template>
  <div class="order-detail">
    <div class="page-banner">
      <div class="banner-inner">
        <div class="banner-top">
          <div class="banner-info">
            <h2 class="banner-title"><i class="el-icon-s-order" />全部订单</h2>
            <p class="banner-desc">查看和管理所有订单</p>
          </div>
        </div>
      </div>
    </div>

    <div class="order-body all-order-body">
      <div class="order-main">
        <!-- 搜索面板 -->
        <div class="search-card all-search-card">
          <el-form :model="searchForm" label-width="130px" size="small">
            <el-row :gutter="20">
              <el-col :span="5">
                <el-form-item label="业务类型:">
                  <el-select v-model="searchForm.orderType" placeholder="请选择业务类型" style="min-width: 100%" clearable>
                    <el-option label="入仓" :value="1" />
                    <el-option label="代发" :value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="品牌:">
                  <el-input v-model.trim="searchForm.brand" placeholder="请输入品牌" @keyup.enter.native="handleSearch" />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="品类:">
                  <el-input v-model.trim="searchForm.category" placeholder="请输入品类" @keyup.enter.native="handleSearch" />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="订单状态:">
                  <el-select v-model="searchForm.status" placeholder="请选择订单状态" style="min-width: 100%" clearable>
                    <el-option v-for="item in optionStatus" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="发货时间:">
                  <el-date-picker v-model="searchForm.sendTimeRange" type="datetimerange" range-separator="至" start-placeholder="开始日期时间" end-placeholder="结束日期时间" style="max-width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="签收时间:">
                  <el-date-picker v-model="searchForm.signedTimeRange" type="datetimerange" range-separator="至" start-placeholder="开始日期时间" end-placeholder="结束日期时间" style="max-width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item style="text-align: end;">
                  <el-button type="primary" class="search-btn-primary" @click="handleSearch">搜索</el-button>
                  <el-button class="search-btn-default" @click="handleReset">重置</el-button>
                  <el-button class="search-btn-default" @click="handleBatchQuery">批量查询</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <!-- 订单表格 -->
        <div class="table-card">
          <el-table
            v-loading="loading"
            height="100%"
            :data="orderList"
            size="small"
            border
            stripe
            class="all-table"
          >
            <template slot="empty">
              <div class="no-data-content">
                <i class="el-icon-document" />
                <p>暂无订单数据</p>
              </div>
            </template>
            <el-table-column prop="orderStyle" label="订单类型" width="90" fixed="left" align="center">
              <template slot-scope="scope"><OrderStyleBadge :order-style="scope.row.orderStyle" /></template>
            </el-table-column>
            <el-table-column prop="orderCode" label="订单编号" min-width="180" fixed="left">
              <template slot-scope="scope">
                <span>{{ scope.row.orderCode || '-' }}</span>
                <i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon" title="复制" @click="handleCopyText(scope.row.orderCode)" />
              </template>
            </el-table-column>
            <el-table-column prop="orderType" label="业务类型" width="80" align="center">
              <template slot-scope="scope"><el-tag :type="scope.row.orderType === 1 ? 'success' : 'warning'" size="mini">{{ scope.row.orderType === 1 ? '入仓' : '代发' }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="brand" label="品牌/品类" min-width="150">
              <template slot-scope="scope">
                <div class="order-productName"><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.brand || '-' }}</span></div><div class="order-productName-line"><span v-if="scope.row.category" class="pn-model">{{ scope.row.category }}</span></div></div>
              </template>
            </el-table-column>
            <el-table-column label="产品型号" prop="productName" min-width="200" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <div class="order-productName"><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.productName || '-' }}</span></div><div class="order-productName-line"><span v-if="scope.row.skuName" class="pn-model">{{ scope.row.skuName }}</span></div></div>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" min-width="100" align="center" sortable />
            <el-table-column prop="tradePrice" label="价格" min-width="120" align="right">
              <template slot-scope="scope"><span class="price-text">¥{{ scope.row.tradePrice || '-' }}</span></template>
            </el-table-column>
            <el-table-column prop="accountingPeriod" label="账期" min-width="100" align="center" />
            <el-table-column prop="status" label="订单状态" min-width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)" size="small" effect="light" class="status-tag">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="收件人信息" prop="address" min-width="200" :show-overflow-tooltip="true" align="center">
              <template slot-scope="scope">
                <el-popover placement="top" trigger="hover" popper-class="addr-popover" :open-delay="150" :close-delay="100">
                  <div class="addr-content"><div class="addr-line main">收件人: {{ scope.row.addressee || '无' }}</div><div class="addr-line sub">电话: {{ scope.row.phone || '无' }}</div><div class="addr-line sub">地址: {{ scope.row.receivingAddress || '无' }}<i v-if="scope.row.receivingAddress" class="el-icon-copy-document copy-icon" @click="copyText(`收件人:${scope.row.addressee};电话:${scope.row.phone};地址:${scope.row.receivingAddress}`)" /></div></div>
                  <span slot="reference">{{ scope.row.provinceName }} {{ scope.row.cityName }}</span>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="shipmentsTime" label="发货时间" min-width="200" align="center">
              <template slot-scope="scope"><span>{{ scope.row.sendTime || '--' }}</span></template>
            </el-table-column>
            <el-table-column prop="signedTime" label="签收时间" min-width="200" align="center">
              <template slot-scope="scope"><span>{{ scope.row.signedTime || '--' }}</span></template>
            </el-table-column>
            <el-table-column prop="operation" label="操作" min-width="200" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="mini" class="text-btn" @click="handleViewSerial(scope.row)">查看串码</el-button>
                <el-button type="text" size="mini" class="text-btn" @click="handleViewLogistics(scope.row)">查看物流</el-button>
                <el-button type="text" size="mini" class="text-btn" @click="handleBillDetails(scope.row)">账单详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination :current-page="pagination.current" :page-sizes="[30, 50, 100]" :page-size="pagination.size" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
          </div>
        </div>
      </div>
    </div>

    <ImeiDialog :visible.sync="imeiDialogVisible" :current-order="imeiDialogData" @confirm="handleConfirmImei" @cancel="handleCancelImei" @close="handleCancelImei" />
    <LogisticsDialog :visible.sync="logisticsDialogVisible" :current-order="logisticsDialogData" @confirm="handleConfirmLogistics" @cancel="handleCancelLogistics" @close="handleCancelLogistics" />
    <el-dialog title="批量查询订单" :visible.sync="batchDialogVisible" width="600px" :close-on-click-modal="false" class="batch-dialog">
      <el-form label-width="80px">
        <el-form-item label="订单编码">
          <el-input v-model.trim="batchInput" type="textarea" :rows="6" placeholder="每行一个订单编码，或使用空格、逗号分隔" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="batchDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmBatchQuery">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { throttle } from '@/utils'
import { getOrderAllList } from '@/api/allOrder'
import ImeiDialog from '@/components/ImeiDialog'
import LogisticsDialog from '@/components/LogisticsDialog'
import OrderStyleBadge from '@/components/OrderStyleBadge'

export default {
  name: 'AllOrder',
  components: { ImeiDialog, LogisticsDialog, OrderStyleBadge },
  data() {
    return {
      loading: false,
      orderList: [],
      optionStatus: [
        { label: '发货中', value: 4 }, { label: '当日发货', value: 5 }, { label: '在途', value: 6 },
        { label: '追单', value: 7 }, { label: '异常订单', value: 8 }, { label: '待确定收货', value: 9 },
        { label: '已完成', value: 10 }, { label: '撤销', value: 11 }
      ],
      pagination: { current: 1, size: 30, total: 0 },
      imeiDialogVisible: false, imeiDialogData: {},
      logisticsDialogVisible: false, logisticsDialogData: {},
      searchForm: { brand: '', category: '', orderCodeList: [], sendTimeRange: [], signedTimeRange: [], status: '', orderType: '' },
      batchDialogVisible: false, batchInput: ''
    }
  },
  mounted() { this.fetchOrderList() },
  methods: {
    handleSearch: throttle(function() { this.performSearch() }, 500),
    handleReset() {
      this.searchForm = { brand: '', category: '', orderCodeList: [], sendTimeRange: [], signedTimeRange: [], status: '', orderType: '' }
      this.$message.info('搜索条件已重置')
    },
    handleBatchQuery() {
      if (Array.isArray(this.searchForm.orderCodeList) && this.searchForm.orderCodeList.length) {
        this.batchInput = this.searchForm.orderCodeList.join('\n')
      } else { this.batchInput = '' }
      this.batchDialogVisible = true
    },
    confirmBatchQuery() {
      const raw = (this.batchInput || '').trim()
      if (!raw) { this.$message.warning('请输入至少一个订单编码'); return }
      const orderCodes = raw.split(/[\s,，]+/).map(code => code.trim()).filter(code => code)
      if (!orderCodes.length) { this.$message.warning('请输入有效的订单编码'); return }
      this.searchForm.orderCodeList = orderCodes
      this.pagination.current = 1
      this.fetchOrderList()
      this.batchDialogVisible = false
      this.$message.success(`开始查询 ${orderCodes.length} 个订单`)
    },
    performSearch() { this.pagination.current = 1; this.fetchOrderList() },
    buildSearchParams() {
      const allOrderForm = {}
      allOrderForm.status = this.searchForm.status
      allOrderForm.brand = this.searchForm.brand
      allOrderForm.category = this.searchForm.category
      allOrderForm.orderCodeList = this.searchForm.orderCodeList
      if (this.searchForm.orderType) allOrderForm.orderType = this.searchForm.orderType
      if (this.searchForm.sendTimeRange.length > 0) {
        allOrderForm.sendStartTime = this.searchForm.sendTimeRange[0]
        allOrderForm.sendEndTime = this.searchForm.sendTimeRange[1]
      } else { allOrderForm.sendStartTime = ''; allOrderForm.sendEndTime = '' }
      if (this.searchForm.signedTimeRange.length > 0) {
        allOrderForm.signedStartTime = this.searchForm.signedTimeRange[0]
        allOrderForm.signedEndTime = this.searchForm.signedTimeRange[1]
      } else { allOrderForm.signedStartTime = ''; allOrderForm.signedEndTime = '' }
      return allOrderForm
    },
    async fetchOrderList() {
      const searchParams = this.buildSearchParams()
      this.loading = true
      try {
        const response = await getOrderAllList(searchParams, { pageNum: this.pagination.current, pageSize: this.pagination.size })
        if (response && response.rows) { this.orderList = response.rows || []; this.pagination.total = response.total || 0 }
        else { this.orderList = []; this.pagination.total = 0 }
      } catch (error) {
        console.error('获取订单列表失败:', error); this.$message.error('获取订单列表失败，请稍后重试')
        this.orderList = []; this.pagination.total = 0
      } finally { this.loading = false }
    },
    handleSizeChange(size) { this.pagination.size = size; this.fetchOrderList() },
    handleCurrentChange(current) { this.pagination.current = current; this.fetchOrderList() },
    getStatusType(status) {
      return { 4: 'warning', 5: 'success', 6: 'info', 7: 'warning', 8: 'danger', 9: 'info', 10: 'success', 11: 'danger' }[status] || 'info'
    },
    getStatusText(status) {
      return { 4: '发货中', 5: '当日发货', 6: '在途', 7: '追单', 8: '异常订单', 9: '待确定收货', 10: '已完成', 11: '撤销', 12: '售后' }[status] || '未知'
    },
    getSubStatusText(subStatus) { return subStatus || '-' },
    handleCopyText(text) {
      if (navigator.clipboard) { navigator.clipboard.writeText(text).then(() => this.$message.success('复制成功')).catch(() => this.fallbackCopyText(text)) } else { this.fallbackCopyText(text) }
    },
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea'); textArea.value = text; document.body.appendChild(textArea); textArea.focus(); textArea.select()
      try { document.execCommand('copy'); this.$message.success('复制成功') } catch (err) { this.$message.error('复制失败') }
      document.body.removeChild(textArea)
    },
    handleViewSerial(row) { this.imeiDialogData = { ...row }; this.imeiDialogVisible = true },
    handleViewLogistics(row) { this.logisticsDialogData = { ...row }; this.logisticsDialogVisible = true },
    handleConfirmImei() { this.imeiDialogVisible = false },
    handleCancelImei() { this.imeiDialogVisible = false },
    handleConfirmLogistics() { this.logisticsDialogVisible = false },
    handleCancelLogistics() { this.logisticsDialogVisible = false },
    handleBillDetails(row) { this.$message.info(`订单 ${row.noWarehouseOrderNumber} 的账单详情`) },
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) { navigator.clipboard.writeText(text).then(() => this.$message.success('复制成功')).catch(() => this.fallbackCopyText(text)) } else { this.fallbackCopyText(text) }
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/order-modern.scss';
</style>

<style lang="scss" scoped>
.all-order-body {
  .all-search-card {
    .el-form-item { margin-bottom: 0; }
    .el-row + .el-row { margin-top: 16px; }
  }
  .all-table {
    &.el-table--border { border: none; }
  }
  .text-btn {
    color: #2563FF;
    font-size: 12px;
    padding: 0 4px;
    &:hover { color: #1D4ED8; }
  }
  .no-data-content {
    text-align: center; padding: 40px 0; color: #909399;
    i { font-size: 48px; margin-bottom: 16px; display: block; }
    p { margin: 0; font-size: 16px; }
  }
}
</style>
