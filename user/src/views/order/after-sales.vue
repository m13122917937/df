<!--待发货-->
<template>
  <div class="order-detail pending-order">
    <!-- 页面头部操作区 -->
    <div class="page-header">
      <div class="header-left">
        <div class="page-title">
          <i class="el-icon-s-order" />
          售后订单管理
        </div>
      </div>
    </div>

    <!-- 订单内容布局 -->
    <div class="order-content-layout">
      <!-- 筛选面板 -->
      <div class="filter-panels">
        <!-- 地区筛选 -->
        <RegionSidebar
          ref="regionSidebar"
          :status="currentStatus"
          :current-value="selectedRegion"
          :current-brand="selectedBrand"
          @change="handleRegionChange"
        />

        <!-- 品牌筛选 -->
      </div>
      <!-- 订单表格容器 -->
      <div class="order-table-container-wrapper">
        <BrandFilter
          ref="brandFilter"
          :status="currentStatus"
          :current-brand="selectedBrand"
          :province-id="selectedRegion"
          @brand-change="handleBrandChange"
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
            <el-table-column prop="deliveryTime" label="发货时效" width="120" align="center">
              <template slot-scope="scope">
                {{ getDeliveryTimeText(scope.row.deliveryDeadline) }}
              </template>
            </el-table-column>
            <el-table-column prop="subStatus" label="发货状态" width="120" align="center">
              <template slot-scope="scope">
                <div style="color: red;">
                  {{ getsubStatusText(scope.row.subStatus) }}
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
              align="center"
              :show-overflow-tooltip="true"
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
            <el-table-column prop="tradeTime" label="抢单时间" width="200" align="center">
              <template slot-scope="scope">
                {{ scope.row.tradeTime || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="tradeUserName" label="抢单人" width="120" align="center">
              <template slot-scope="scope">
                {{ scope.row.tradeUserName || '-' }}
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
      @close="handleCloseImportShipping"
    />
  </div>
</template>

<script>
import ShippingRulesDialog from '@/components/ShippingRulesDialog'
import ImportShippingDialog from '@/components/ImportShippingDialog'
import EmptyState from '@/components/EmptyState'
import RegionSidebar from '@/components/RegionSidebar'
import BrandFilter from '@/components/brandFilter'
import OrderSearch from '@/components/OrderSearch'
import OrderStyleBadge from '@/components/OrderStyleBadge'

import { getAfterSalesList } from '@/api/order'
import { getDeliveryTimeText } from '@/utils/deliveryTime'

export default {
  name: 'TodayOrder',
  components: {
    ShippingRulesDialog,
    ImportShippingDialog,
    EmptyState,
    RegionSidebar,
    BrandFilter,
    OrderSearch,
    OrderStyleBadge
  },
  data() {
    return {
      loading: false,
      orderList: [],
      pagination: {
        current: 1,
        size: 30,
        total: 0
      },
      // 地区筛选相关数据
      selectedRegion: '',
      // 品牌筛选相关数据
      selectedBrand: '',
      // 当前状态
      currentStatus: 12,
      // 搜索表单
      searchForm: {
        orderCode: '',
        productName: '',
        skuName: ''
      },
      // 发货规则弹窗相关数据
      shippingRulesDialogVisible: false,
      // 导入发货信息弹窗相关数据
      importShippingDialogVisible: false,
      bulkShippingList: [],
      failedOrdersCount: 0
    }
  },
  computed: {
  },
  mounted() {
    this.fetchOrderList()
  },
  methods: {
    // 发货时效显示方法
    getDeliveryTimeText,
    // 发货状态显示方法
    getsubStatusText(subStatus) {
      const map = {
        41: '待填写串码',
        42: '待填写物流信息',
        43: '平台二销验证中'
      }
      console.log(subStatus)

      return map[subStatus] || '-'
    },
    handleRegionChange(region) {
      console.log('区域切换:', region)
      this.selectedRegion = region
      this.pagination.current = 1
      // 重新获取订单列表
      this.fetchOrderList()
    },
    handleBrandChange(brand) {
      console.log('品牌切换:', brand)
      this.selectedBrand = brand
      this.pagination.current = 1
      // 重新获取订单列表
      this.fetchOrderList()
    },
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
      const res = await getAfterSalesList({
        province: this.selectedRegion,
        brand: this.selectedBrand,
        orderCode: this.searchForm.orderCode,
        productName: this.searchForm.productName,
        skuName: this.searchForm.skuName
      }, {
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
    // 账期类型相关方法
    getAccountingPeriodTypeText(accountingPeriod) {
      const typeMap = {
        0: '0天',
        1: '1天',
        2: '2天',
        3: '3天'
      }
      return typeMap[accountingPeriod] || '未知'
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
    },
    handleCloseShippingRules() {
      this.shippingRulesDialogVisible = false
    },
    // 导入发货信息相关方法
    handleExportPendingOrders() {
      this.$message.success('导出待发货订单功能')
      // 这里可以实现导出Excel功能
    },
    handleImportShippingInfo() {
      this.$message.success('导入发货信息功能')
      // 这里可以实现文件上传功能
    },
    handleRefreshImportStatus() {
      this.$message.info('刷新导入状态')
      // 这里可以实现刷新失败订单数据
    },
    handleCloseImportShipping() {
      this.importShippingDialogVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/table-common.scss';

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
