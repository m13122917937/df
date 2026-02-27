<!--完成拣货-->
<template>
  <div class="order-detail today-shipped-order">
    <!-- 订单内容布局 -->
    <div class="order-content-layout">
      <div class="order-table-container-wrapper">
        <!-- 品牌筛选 -->
        <BrandFilter
          ref="brandFilter"
          :status="currentStatus"
          :current-brand="selectedBrand"
          :province-id="selectedRegion"
          @change="handleBrandChange"
        />
        <OrderSearch
          :value="searchForm"
          @search="handleSearch"
          @reset="handleReset"
        >
        
        </OrderSearch>
        <!-- 订单表格 -->
        <div class="order-table-container table-section">
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
              <EmptyState text="暂无完成捡货订单数据" />
            </template>
            <el-table-column
              label="订单编号"
              prop="orderCode"
              min-width="200"
              fixed="left"
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
                    ></i>
                  </div>
                </div>
              </template>
            </el-table-column>
             <el-table-column prop="tradeCompanyName" label="供应商" width="300" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                {{ scope.row.tradeCompanyName || '-' }}
              </template>
            </el-table-column>
            <el-table-column
              label="采购人/成交时间"
              prop="createTime"
              min-width="200"
            >
              <template slot-scope="scope">
                <div class="order-numbers">
                  <div class="order-number-item">
                    {{ scope.row.tradeUserName || "-" }}
                  </div>
                  <div class="order-number-item">
                     {{
                      scope.row.createTime || "-"
                    }}
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
            <el-table-column prop="quantity" label="入库数量/成交数量" width="180" align="center" >
              <template slot-scope="scope">
                {{ scope.row.warehouseQuantity || 0 }} / {{ scope.row.quantity || 0 }}
              </template>
            </el-table-column>
            <el-table-column label="成交价" prop="tradePrice" min-width="120" align="center" />
            <el-table-column prop="deliveryCode" label="送货码" width="120" align="center" >
              <template slot-scope="scope">
                {{ scope.row.deliveryCode || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="deliveryCode" label="备注" width="120" align="center" >
              <template slot-scope="scope">
                {{ scope.row.remark || '-' }}
              </template>
            </el-table-column>
            <!-- 操作列已移除 -->
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

    <!-- 操作相关弹窗已移除 -->

  </div>
</template>

<script>
import EmptyState from './components/EmptyState'
import BrandFilter from './components/brandFilter'
import OrderSearch from './components/OrderSearch'
import { getPickList } from '@/api/pickManage'
import { getDeliveryTimeText } from '@/utils/deliveryTime'

export default {
  name: 'TodayOrder',
    components: {
    EmptyState,
    BrandFilter,
    OrderSearch,
  },
  data() {
    return {
      loading: false,
      orderList: [],
      selectedRegion: '',
      selectedBrand: '',
      currentStatus: 10, 
      // 搜索表单
      searchForm: {
        productName: '',
        companyId: '',
        deliveryCode: ''
      },
      pagination: {
        current: 1,
        size: 30,
        total: 0
      },
      // 操作相关弹窗数据已移除
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
    handleRegionChange(region) {
      this.selectedRegion = region
      this.pagination.current = 1
      // 重新获取订单列表
      this.fetchOrderList()
    },
    handleBrandChange(brand) {
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
      const res = await getPickList({
        // province: this.selectedRegion,
        brand: this.selectedBrand,
        companyId: this.searchForm.companyId,
        productName: this.searchForm.productName,
        deliveryCode: this.searchForm.deliveryCode,
        statusList: [this.currentStatus]
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
    handleGrab(row) {
      this.$message.success(`抢单成功：${row.orderNo}`)
    },
    handleView(row) {
      this.$message.info(`查看订单：${row.orderNo}`)
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
    },
    // 标签筛选相关方法
    handleTagFilterConfirm() {
      this.$message.success('标签筛选已应用')
    },
    handleTagFilterCancel() {
      console.log('取消标签筛选')
    },
    // 订单编号搜索相关方法
    handleOrderNumberFilterConfirm() {
      this.$message.success('订单编号搜索已应用')
    },
    handleOrderNumberFilterCancel() {
      console.log('取消订单编号搜索')
    },
    // 发货时效筛选相关方法
    handleShippingTimeFilterConfirm() {
      this.$message.success('发货时效筛选已应用')
    },
    handleShippingTimeFilterCancel() {
      console.log('取消发货时效筛选')
    },
    // 到货时效筛选相关方法
    handleArrivalTimeFilterConfirm() {
      this.$message.success('到货时效筛选已应用')
    },
    handleArrivalTimeFilterCancel() {
      console.log('取消到货时效筛选')
    },
    // 产品型号筛选相关方法
    handleProductFilterConfirm() {
      this.$message.success('产品型号筛选已应用')
    },
    handleProductFilterCancel() {
      console.log('取消产品型号筛选')
    },
    // 揽收状态筛选相关方法
    handlePickupStatusFilterConfirm() {
      this.$message.success('揽收状态筛选已应用')
    },
    handlePickupStatusFilterCancel() {
      console.log('取消揽收状态筛选')
    },
    // 操作相关方法已移除

  }
}
</script>

<style lang="scss" scoped>
@import './style/table-common.scss';

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

/* 操作按钮样式 */
.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
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
.pending-order {
  // 订单内容布局
  .order-content-layout {
    display: flex;
    gap: 16px;
    flex: 1;
    min-height: 0; // 确保flex子元素可以收缩
  }

  .order-table-container {
    flex: 1;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }

  // 响应式布局
  @media (max-width: 1200px) {
    .order-content-layout {
      flex-direction: column;
      gap: 12px;
    }

    .order-table-container {
      overflow-x: auto;
    }
  }

  @media (max-width: 768px) {
    .page-header {
      flex-direction: column;
      gap: 16px;
      text-align: center;

      .header-actions {
        justify-content: center;
      }
    }

    .order-content-layout {
      margin-bottom: 16px;
    }

    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
      padding: 20px;
      background: #fff;
      border-radius: 6px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      border: 1px solid #f0f0f0;

      .custom-pagination {
        .el-pagination__sizes,
        .el-pagination__jump {
          display: none;
        }
      }
    }
  }

  @media (max-width: 480px) {
    .page-header {
      padding: 16px;
      margin-bottom: 16px;

      .page-title {
        font-size: 18px;
      }

      .page-subtitle {
        font-size: 12px;
      }
    }

    .operation-buttons {
      flex-direction: column;
      gap: 4px;

      .el-button {
        width: 100%;
      }
    }
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
  display: flex;
  gap: 8px;
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

.new-order-form ::v-deep .el-select {
  width: 100%;
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
