<!--在途-->
<template>
  <div class="order-detail transit-order">
    <!-- 页面头部操作区 -->
    <div class="page-header">
      <div class="header-left">
        <div class="page-title">
          <i class="el-icon-s-order" />
          在途订单管理
        </div>
        <!-- <span class="page-subtitle">管理在途订单，处理发货流程</span> -->
      </div>
      <div class="header-actions">
        <!-- <el-button
          type="warning"
          icon="el-icon-edit"
          class="action-btn"
          @click="handleSerialAdjustment"
        >
          串码调整
        </el-button> -->
      </div>
    </div>
    <!-- 订单内容布局 -->
    <div class="order-content-layout">
      <!-- 筛选面板 -->
      <div class="filter-panels">
        <!-- 地区筛选 -->
        <RegionSidebar
          :status="currentStatus"
          :current-value="selectedRegion"
          :current-brand="selectedBrand"
          @change="handleRegionChange"
        />
      </div>
      <div class="order-table-container-wrapper">
        <!-- 品牌筛选 -->
        <BrandFilter
          :status="currentStatus"
          :current-brand="selectedBrand"
          :province-id="selectedRegion"
          @change="handleBrandChange"
        />
        <OrderSearch
          :value="searchForm"
          @search="handleSearch"
          @reset="handleReset"
        />
        <!-- 订单表格 -->
        <div class="order-table-container">
          <el-table
            ref="table"
            v-loading="loading"
            :data="orderList"
            stripe
            size="medium"
            center
            style="width: 100%"
            :fit="true"
            height="100%"
            :header-cell-style="{
              background: '#f7f8fa',
              color: '#606266',
              fontWeight: 600,
            }"
            :cell-style="{ padding: '8px 0' }"
          >
            <!-- 空数据状态 -->
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
            <el-table-column prop="orderStyle" label="订单类型" width="90" fixed="left" align="center">
              <template slot-scope="scope">
                <OrderStyleBadge
                  :order-style="scope.row.orderStyle"
                />
              </template>
            </el-table-column>
            <!-- 单号：(订单编码) + (原始订单号) -->
            <el-table-column
              label="单号"
              prop="orderCode"
              min-width="200"
              fixed="left"
              align="center"
            >
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">
                    {{
                      scope.row.orderCode || "-"
                    }}
                    <i
                      v-if="scope.row.orderCode"
                      class="el-icon-copy-document copy-icon"
                      @click="copyText(scope.row.orderCode)"
                    />
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="brand" label="品牌/品类" width="120" align="center">
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.brand || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.category" class="pn-model">{{
                      scope.row.category
                    }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <!-- 产品型号 -->
            <el-table-column
              label="产品型号"
              prop="productName"
              min-width="200"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <div class="order-productName">
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.productName || "-"
                    }}</span>
                  </div>
                  <div class="order-productName-line">
                    <span v-if="scope.row.skuName" class="pn-model">{{
                      scope.row.skuName
                    }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="120" align="center" sortable>
              <template slot-scope="scope">
                {{ scope.row.quantity || 0 }}
              </template>
            </el-table-column>
            <!-- 成交价格 -->
            <el-table-column label="成交价" prop="tradePrice" min-width="120" align="center" />
            <!-- 账期 -->
            <el-table-column
              label="账期"
              prop="accountingPeriod"
              min-width="130"
              align="center"
            >
              <template slot-scope="scope">
                <span>{{
                  formatAccountingPeriod(scope.row.accountingPeriod)
                }}</span>
              </template>
            </el-table-column>
            <!-- 收货地（悬浮查看收件信息） -->
            <el-table-column
              label="收货地"
              prop="address"
              min-width="200"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <el-popover
                  placement="top"
                  trigger="hover"
                  popper-class="addr-popover"
                  :open-delay="150"
                  :close-delay="100"
                >
                  <div class="addr-content">
                    <div class="addr-line main">
                      收件人: {{ scope.row.addressee || "无" }}
                    </div>
                    <div class="addr-line sub">
                      电话: {{ scope.row.phone || "无" }}
                    </div>
                    <div class="addr-line sub">
                      地址: {{ scope.row.receivingAddress || "无" }}
                      <i
                        v-if="scope.row.receivingAddress"
                        class="el-icon-copy-document copy-icon"
                        @click="copyText(`收件人:${scope.row.addressee};电话:${scope.row.phone};地址:${scope.row.receivingAddress}`)"
                      />
                    </div>
                  </div>
                  <span slot="reference">{{ scope.row.provinceName }} {{ scope.row.cityName }}</span>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="sendTime" label="发货时间" width="200" align="center">
              <template slot-scope="scope">
                {{ scope.row.sendTime || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="trackingCompany" label="物流信息" width="240" align="center">
              <template slot-scope="scope">
                <TrackingInfo
                  :company="scope.row.trackingCompany"
                  :number="scope.row.trackingNumber"
                  :data="scope.row"
                  @click="handleLogisticsInfo"
                />
              </template>
            </el-table-column>
            <!-- 操作 -->
            <el-table-column label="操作" width="160" fixed="right" align="center">
              <template slot-scope="scope">
                <div class="operation-buttons">
                  <el-button v-if="scope.row.orderType !== 1" size="mini" type="primary" @click="handleImeiInfo(scope.row)">串码信息</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页器 -->
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

    <!-- 串码调整弹窗 -->
    <SerialAdjustmentDialog
      :visible.sync="serialAdjustmentDialogVisible"
      @confirm="handleConfirmSerialAdjustment"
      @cancel="handleCancelSerialAdjustment"
      @close="handleCancelSerialAdjustment"
    />

    <!-- 串码弹窗 -->
    <ImeiDialog
      :visible.sync="imeiDialogVisible"
      :current-order="imeiDialogData"
      @confirm="handleConfirmImei"
      @cancel="handleCancelImei"
      @close="handleCancelImei"
    />

    <!-- 物流弹窗 -->
    <LogisticsDialog
      :visible.sync="logisticsDialogVisible"
      :current-order="logisticsDialogData"
      @confirm="handleConfirmLogistics"
      @cancel="handleCancelLogistics"
      @close="handleCancelLogistics"
    />
  </div>
</template>

<script>
import SerialAdjustmentDialog from '@/components/SerialAdjustmentDialog'
import EmptyState from '@/components/EmptyState'
import ImeiDialog from '@/components/ImeiDialog'
import LogisticsDialog from '@/components/LogisticsDialog'
import RegionSidebar from '@/components/RegionSidebar'
import BrandFilter from '@/components/brandFilter'
import OrderSearch from '@/components/OrderSearch'
import TrackingInfo from '@/components/TrackingInfo'
import OrderStyleBadge from '@/components/OrderStyleBadge'
import { getTransitList } from '@/api/order'

export default {
  name: 'TodayOrder',
  components: {
    SerialAdjustmentDialog,
    EmptyState,
    ImeiDialog,
    LogisticsDialog,
    RegionSidebar,
    BrandFilter,
    OrderSearch,
    TrackingInfo,
    OrderStyleBadge
  },
  data() {
    return {
      loading: false,
      orderList: [],
      selectedRegion: '',
      selectedBrand: '',
      currentStatus: 6, // 在途状态
      // 搜索表单
      searchForm: {
        orderCode: '',
        productName: '',
        skuName: '',
        orderType: ''
      },
      pagination: {
        current: 1,
        size: 30,
        total: 0
      },
      // 串码调整弹窗相关数据
      serialAdjustmentDialogVisible: false,
      // 物流弹窗相关数据
      logisticsDialogVisible: false,
      // 物流弹窗相关数据
      logisticsDialogData: {},
      // 串码弹窗相关数据
      imeiDialogVisible: false,
      // 串码弹窗相关数据
      imeiDialogData: {}
    }
  },
  computed: {
  },
  mounted() {
    this.fetchOrderList()
  },
  methods: {
    // 搜索处理
    handleSearch(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.current = 1
      this.fetchOrderList()
    },
    // 重置搜索
    handleReset(searchData) {
      this.searchForm = { ...searchData }
      this.pagination.current = 1
      this.fetchOrderList()
    },
    async fetchOrderList() {
      this.loading = true
      const params = {
        province: this.selectedRegion,
        brand: this.selectedBrand,
        orderCode: this.searchForm.orderCode,
        productName: this.searchForm.productName,
        skuName: this.searchForm.skuName
      }
      if (this.searchForm.orderType) {
        params.orderType = this.searchForm.orderType
      }
      const res = await getTransitList(params, {
        pageNum: this.pagination.current,
        pageSize: this.pagination.size
      })
      if (res && res.code === 200) {
        this.orderList = res.rows
        this.pagination.total = res.total
      } else {
        this.$message.error(res?.msg || '获取订单列表失败')
      }
      this.loading = false
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.fetchOrderList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.fetchOrderList()
    },
    handleGrab(row) {
      this.$message.success(`抢单成功：${row.orderNo}`)
    },
    handleView(row) {
      this.$message.info(`查看订单：${row.orderNo}`)
    },
    // 标签筛选相关方法
    handleTagFilterConfirm(selectedObjects) {
      console.log('选中的标签:', selectedObjects)
      this.$message.success('标签筛选已应用')
    },
    handleTagFilterCancel() {
      console.log('取消标签筛选')
    },
    // 订单编号搜索相关方法
    handleOrderNumberFilterConfirm(selectedObjects) {
      console.log('选中的订单编号:', selectedObjects)
      this.$message.success('订单编号搜索已应用')
    },
    handleOrderNumberFilterCancel() {
      console.log('取消订单编号搜索')
    },
    // 发货时效筛选相关方法
    handleShippingTimeFilterConfirm(selectedObjects) {
      console.log('选中的发货时效:', selectedObjects)
      this.$message.success('发货时效筛选已应用')
    },
    handleShippingTimeFilterCancel() {
      console.log('取消发货时效筛选')
    },
    // 到货时效筛选相关方法
    handleArrivalTimeFilterConfirm(selectedObjects) {
      console.log('选中的到货时效:', selectedObjects)
      this.$message.success('到货时效筛选已应用')
    },
    handleArrivalTimeFilterCancel() {
      console.log('取消到货时效筛选')
    },
    // 产品型号筛选相关方法
    handleProductFilterConfirm(selectedObjects) {
      console.log('选中的产品型号:', selectedObjects)
      this.$message.success('产品型号筛选已应用')
    },
    handleProductFilterCancel() {
      console.log('取消产品型号筛选')
    },
    // 串码调整相关方法
    handleSerialAdjustment() {
      this.serialAdjustmentDialogVisible = true
    },
    handleConfirmSerialAdjustment(data) {
      console.log('串码调整数据:', data)
      this.$message.success('串码调整申请已提交')
      this.serialAdjustmentDialogVisible = false
    },
    handleCancelSerialAdjustment() {
      this.serialAdjustmentDialogVisible = false
    },
    handleImeiInfo(row) {
      this.imeiDialogData = { ...row }
      this.imeiDialogVisible = true
    },
    handleConfirmImei() {
      this.imeiDialogVisible = false
    },
    handleCancelImei() {
      this.imeiDialogVisible = false
    },
    handleLogisticsInfo(row) {
      this.logisticsDialogData = { ...row }
      this.logisticsDialogVisible = true
    },
    handleConfirmLogistics() {
      this.logisticsDialogVisible = false
    },
    handleCancelLogistics() {
      this.logisticsDialogVisible = false
    },
    // 地区筛选相关方法
    handleRegionChange(regionCode) {
      this.selectedRegion = regionCode
      this.pagination.current = 1
      // 这里可以根据地区重新获取订单数据
      this.fetchOrderList()
    },
    handleBrandChange(brand) {
      this.selectedBrand = brand
      this.pagination.current = 1
      // 重新获取订单列表
      this.fetchOrderList()
    },
    // 串码选项相关方法
    getCodeOptionType(codeOptions) {
      const typeMap = {
        0: 'success', // 发货前提供串码
        1: 'warning', // 发货后提供串码
        2: 'info' // 不需要串码
      }
      return typeMap[codeOptions] || 'info'
    },
    getCodeOptionText(codeOptions) {
      const textMap = {
        0: '发货前提供',
        1: '发货后提供',
        2: '不需要'
      }
      return textMap[codeOptions] || '未知'
    },
    // 格式化账期
    formatAccountingPeriod(val) {
      const n = Number(val)
      if (!isFinite(n) || n < 0) return '-'
      return n === 0 ? '当天' : `T+${n}`
    },
    // 复制文本到剪贴板
    copyText(text) {
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('复制成功')
        }).catch(() => {
          this.fallbackCopyText(text)
        })
      } else {
        this.fallbackCopyText(text)
      }
    },
    // 降级复制方法
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea')
      textArea.value = text
      textArea.style.position = 'fixed'
      textArea.style.left = '-999999px'
      textArea.style.top = '-999999px'
      document.body.appendChild(textArea)
      textArea.focus()
      textArea.select()
      try {
        document.execCommand('copy')
        this.$message.success('复制成功')
      } catch (err) {
        this.$message.error('复制失败')
      }
      document.body.removeChild(textArea)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/table-common.scss';

// 筛选面板布局
.filter-panels {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

/* 订单信息样式 */
.order-numbers {
  display: flex;
  flex-direction: column;

  .order-number-item {
    padding: 0;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 12px;
    line-height: 20px;
  }
}

// 产品型号样式
.order-productName {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: center;
}

.order-productName-line {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  line-height: 20px;
}

.order-sku-line {
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: #909399;
}

/* 复制图标样式 */
.copy-icon {
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
  margin-left: 4px;
  transition: color 0.3s;

  &:hover {
    color: #66b1ff;
  }
}

/* 地址弹框样式 */
:deep(.addr-popover) {
  max-width: 520px;
  padding: 12px;
}

.addr-content {
  min-width: 200px;
}

.addr-line {
  line-height: 22px;
  margin-bottom: 4px;

  &.main {
    font-size: 14px;
    color: #303133;
    font-weight: 500;
  }

  &.sub {
    font-size: 13px;
    color: #606266;
  }
}

// 弹窗样式优化
::v-deep .el-dialog {
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 20px 24px;
    border-radius: 12px 12px 0 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;

        &:hover {
          color: #ffd700;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 20px 24px;
    background: #fafbfc;
    border-radius: 0 0 12px 12px;
    border-top: 1px solid #e4e7ed;

    .el-button {
      padding: 10px 20px;
      border-radius: 8px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
    }
  }
}

.operation-buttons {
  margin-bottom: 8px;

  .el-button {
    border-radius: 6px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    &.el-button--mini {
      padding: 6px 12px;
    font-size: 12px;
    }
  }
}

.status-message {
  color: #f56c6c;
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
  padding: 8px 0;
  background: rgba(245, 108, 108, 0.1);
  border-radius: 4px;
  border-left: 3px solid #f56c6c;
  margin-top: 8px;
}

// 动画效果
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes shake {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(2px);
  }
}

.status-text {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;

  .status-icon {
    font-size: 12px;
    margin-left: 4px;
    opacity: 0.7;
    transition: all 0.3s ease;
  }

  &:hover {
    color: #409EFF;

    .status-icon {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}

.status-tooltip-content {
  p {
    margin: 0 0 8px 0;
    line-height: 1.5;
    font-size: 13px;

    &:last-child {
      margin-bottom: 0;
    }

    strong {
      color: #e6a23c;
    }
  }
}
</style>

<style>
/* 全局状态popover样式 */
.status-popover {
  background: #606266 !important;
  color: #fff !important;
  border: none !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3) !important;
}

.status-popover .el-popover__arrow {
  border-top-color: #606266 !important;
}

.status-popover .status-tooltip-content {
  color: #fff;
}

.status-popover .status-tooltip-content p strong {
  color: #ffd700 !important;
}

</style>
