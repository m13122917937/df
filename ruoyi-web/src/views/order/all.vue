<template>
  <div class="all-order">
    <div class="order-content">
      <!-- 搜索面板 -->
      <div class="search-panel">
        <el-form :model="searchForm" label-width="130px" size="small">
          <!-- 第一行 -->
          <el-row :gutter="20">
            <el-col :span="5">
              <el-form-item label="业务类型:">
                <el-select
                  v-model="searchForm.orderType"
                  placeholder="请选择业务类型"
                  style="min-width: 100%"
                  clearable
                >
                  <el-option label="入仓" :value="1" />
                  <el-option label="代发" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="品牌:">
                <el-input
                  v-model.trim="searchForm.brand"
                  placeholder="请输入品牌"
                  @keyup.enter.native="handleSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="品类:">
                <el-input
                  v-model.trim="searchForm.category"
                  placeholder="请输入品类"
                  @keyup.enter.native="handleSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="订单状态:">
                <el-select
                  v-model="searchForm.status"
                  placeholder="请选择订单状态"
                  style="min-width: 100%"
                  clearable
                >
                  <el-option
                    v-for="item in optionStatus"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 第二行 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="发货时间:">
                <el-date-picker
                  v-model="searchForm.sendTimeRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期时间"
                  end-placeholder="结束日期时间"
                  style="max-width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="签收时间:">
                <el-date-picker
                  v-model="searchForm.signedTimeRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期时间"
                  end-placeholder="结束日期时间"
                  style="max-width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 第三行 -->
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item style="text-align: end;">
                <el-button class="search-btn" type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
                <el-button @click="handleBatchQuery">批量查询</el-button>
              </el-form-item>
            </el-col>
          </el-row>

        </el-form>
      </div>

      <!-- 订单表格 -->
      <div class="order-table-container">
        <el-table
          v-loading="loading"
          height="100%"
          :data="orderList"
          size="small"
          :cell-style="{ padding: '8px 0' }"
          :header-cell-style="{
            background: '#f7f8fa',
            color: '#606266',
            fontWeight: 600,
            fontSize: '14px',
          }"
          border
          stripe
          class="order-table"
        >
          <!-- 空数据状态 -->
          <template slot="empty">
            <div class="no-data-content">
              <i class="el-icon-document" />
              <p>暂无订单数据</p>
            </div>
          </template>
          <!-- 订单编号、采购类型、品牌/品类、产品型号、数量、价格、账期、订单状态、收件人信息、发货时间、签收时间 -->
          <!-- 表格列 -->
          <el-table-column prop="orderStyle" label="订单类型" width="90" fixed="left" align="center">
            <template slot-scope="scope">
              <OrderStyleBadge
                :order-style="scope.row.orderStyle"
              />
            </template>
          </el-table-column>
          <!-- 订单编号 -->
          <el-table-column prop="orderCode" label="订单编号" min-width="180" fixed="left">
            <template slot-scope="scope">
              <span>{{ scope.row.orderCode || '-' }}</span>
              <i v-if="scope.row.orderCode" class="el-icon-copy-document copy-icon" title="复制" @click="handleCopyText(scope.row.orderCode)" />
            </template>
          </el-table-column>
          <!-- 业务类型 -->
          <el-table-column prop="orderType" label="业务类型" width="80" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.orderType === 1 ? 'success' : 'warning'" size="mini">
                {{ scope.row.orderType === 1 ? '入仓' : '代发' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 品牌/品类 -->
          <el-table-column prop="brand" label="品牌/品类" min-width="150">
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
          <!-- 数量 -->
          <el-table-column prop="quantity" label="数量" min-width="100" align="center" sortable />

          <!-- 交易价格 -->
          <el-table-column prop="tradePrice" label="价格" min-width="120" align="right">
            <template slot-scope="scope">
              <span class="price-text">¥{{ scope.row.tradePrice || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="accountingPeriod" label="账期" min-width="100" align="center" />
          <!-- 订单状态 -->
          <el-table-column prop="status" label="订单状态" min-width="120" align="center">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="mini">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 收件人 -->
          <el-table-column
            label="收件人信息"
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
          <!-- 发货时间 -->
          <el-table-column prop="shipmentsTime" label="发货时间" min-width="200" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.sendTime || '--' }}</span>
            </template>
          </el-table-column>

          <!-- 签收时间 -->
          <el-table-column prop="signedTime" label="签收时间" min-width="200" align="center">
            <template slot-scope="scope">
              <span>{{ scope.row.signedTime || '--' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="operation" label="操作" min-width="200" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="handleViewSerial(scope.row)">查看串码</el-button>
              <el-button type="text" size="mini" @click="handleViewLogistics(scope.row)">查看物流</el-button>
              <el-button type="text" size="mini" @click="handleBillDetails(scope.row)">账单详情</el-button>
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
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>

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

      <!-- 批量查询弹窗 -->
      <el-dialog
        title="批量查询订单"
        :visible.sync="batchDialogVisible"
        width="600px"
        :close-on-click-modal="false"
      >
        <el-form label-width="80px">
          <el-form-item label="订单编码">
            <el-input
              v-model.trim="batchInput"
              type="textarea"
              :rows="6"
              placeholder="每行一个订单编码，或使用空格、逗号分隔"
            />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="confirmBatchQuery">确 定</el-button>
        </span>
      </el-dialog>
    </div>
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
  components: {
    ImeiDialog,
    LogisticsDialog,
    OrderStyleBadge
  },
  data() {
    return {
      loading: false,
      orderList: [],
      optionStatus: [
        {
          label: '发货中',
          value: 4
        }, {
          label: '当日发货',
          value: 5
        },
        {
          label: '在途',
          value: 6
        },
        {
          label: '追单',
          value: 7
        },
        {
          label: '异常订单',
          value: 8
        },
        {
          label: '待确定收货',
          value: 9
        },
        {
          label: '已完成',
          value: 10
        },
        {
          label: '撤销',
          value: 11
        }
      ],
      pagination: {
        current: 1,
        size: 30,
        total: 0
      },
      // 弹窗相关数据
      imeiDialogVisible: false,
      imeiDialogData: {},
      logisticsDialogVisible: false,
      logisticsDialogData: {},
      // 搜索表单数据
      searchForm: {
        brand: '',
        category: '',
        orderCodeList: [],
        sendTimeRange: [],
        signedTimeRange: [],
        status: '',
        orderType: ''
      },
      // 批量查询弹窗
      batchDialogVisible: false,
      batchInput: ''
    }
  },
  mounted() {
    this.fetchOrderList()
  },
  methods: {
    handleSearch: throttle(function() {
      this.performSearch()
    }, 500),
    // 搜索

    // 重置
    handleReset() {
      this.searchForm = {
        brand: '',
        category: '',
        orderCodeList: [],
        sendTimeRange: [],
        signedTimeRange: [],
        status: '',
        orderType: ''
      }
      this.$message.info('搜索条件已重置')
    },

    // 批量查询
    handleBatchQuery() {
      if (Array.isArray(this.searchForm.orderCodeList) && this.searchForm.orderCodeList.length) {
        this.batchInput = this.searchForm.orderCodeList.join('\n')
      } else {
        this.batchInput = ''
      }
      this.batchDialogVisible = true
    },

    // 批量查询确认
    confirmBatchQuery() {
      const raw = (this.batchInput || '').trim()
      if (!raw) {
        this.$message.warning('请输入至少一个订单编码')
        return
      }

      const orderCodes = raw
        .split(/[\s,，]+/)
        .map(code => code.trim())
        .filter(code => code)

      if (!orderCodes.length) {
        this.$message.warning('请输入有效的订单编码')
        return
      }

      this.searchForm.orderCodeList = orderCodes
      this.pagination.current = 1
      this.fetchOrderList()
      this.batchDialogVisible = false
      this.$message.success(`开始查询 ${orderCodes.length} 个订单`)
    },

    // 执行搜索
    performSearch() {
      // 重置分页到第一页
      this.pagination.current = 1

      // 调用API进行搜索
      this.fetchOrderList()
    },

    // 构建搜索参数 - 按照API文档要求构建allOrderForm对象
    buildSearchParams() {
      const allOrderForm = {}
      // 品牌 - 对应API的brand参数

      allOrderForm.status = this.searchForm.status
      allOrderForm.brand = this.searchForm.brand
      allOrderForm.category = this.searchForm.category
      allOrderForm.orderCodeList = this.searchForm.orderCodeList
      if (this.searchForm.orderType) {
        allOrderForm.orderType = this.searchForm.orderType
      }
      // 发货时间范围 - 对应API的sendStartTime和sendEndTime参数
      if (this.searchForm.sendTimeRange.length > 0) {
        allOrderForm.sendStartTime = this.searchForm.sendTimeRange[0]
        allOrderForm.sendEndTime = this.searchForm.sendTimeRange[1]
      } else {
        allOrderForm.sendStartTime = ''
        allOrderForm.sendEndTime = ''
      }

      // 收货时间范围 - 对应API的signedStartTime和signedEndTime参数
      if (this.searchForm.signedTimeRange.length > 0) {
        allOrderForm.signedStartTime = this.searchForm.signedTimeRange[0]
        allOrderForm.signedEndTime = this.searchForm.signedTimeRange[1]
      } else {
        allOrderForm.signedStartTime = ''
        allOrderForm.signedEndTime = ''
      }
      console.log('allOrderForm', allOrderForm)

      return allOrderForm
    },

    // 获取订单列表
    async fetchOrderList() {
      const searchParams = this.buildSearchParams()
      this.loading = true
      try {
        // 构建API请求参数
        const response = await getOrderAllList(searchParams, {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size
        })

        if (response && response.rows) {
          this.orderList = response.rows || []
          this.pagination.total = response.total || 0
        } else {
          this.orderList = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('获取订单列表失败:', error)
        this.$message.error('获取订单列表失败，请稍后重试')
        this.orderList = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },

    // 分页大小改变
    handleSizeChange(size) {
      this.pagination.size = size
      this.fetchOrderList()
    },

    // 当前页改变
    handleCurrentChange(current) {
      this.pagination.current = current
      this.fetchOrderList()
    },

    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        4: 'warning', // 发货中
        5: 'success', // 当日发货
        6: 'info', // 在途
        7: 'warning', // 追单
        8: 'danger', // 异常订单
        9: 'info', // 待确定收货
        10: 'success', // 已完成
        11: 'danger' // 撤销
      }
      return statusMap[status] || 'info'
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
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
      return statusMap[status] || '未知'
    },

    // 获取子状态文本
    getSubStatusText(subStatus) {
      // 根据实际业务需求添加子状态映射
      return subStatus || '-'
    },

    // 获取撤销类型标签类型
    getRevokeTypeTag(revokeType) {
      const typeMap = {
        0: 'info', // 店铺后台急速退款
        1: 'warning', // 顾客拒签/拒收
        2: 'warning', // 派送未联系到顾客,退回
        3: 'danger', // 24小时物流无揽收信息
        4: 'danger', // 供应商私自拦截
        5: 'success', // 已经从其他渠道发货
        6: 'success' // 手动追单
      }
      return typeMap[revokeType] || 'info'
    },

    // 获取撤销类型文本
    getRevokeTypeText(revokeType) {
      const typeMap = {
        0: '急速退款',
        1: '顾客拒签',
        2: '派送失败',
        3: '无揽收信息',
        4: '私自拦截',
        5: '其他渠道发货',
        6: '手动追单'
      }
      return typeMap[revokeType] || '未知'
    },

    // 复制文本
    handleCopyText(text) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('复制成功')
        }).catch(() => {
          this.fallbackCopyText(text)
        })
      } else {
        this.fallbackCopyText(text)
      }
    },

    // 备用复制方法
    fallbackCopyText(text) {
      const textArea = document.createElement('textarea')
      textArea.value = text
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

    // 处理下拉菜单命令
    handleCommand(command) {
      const { action, row } = command
      switch (action) {
        case 'viewSerial':
          this.handleViewSerial(row)
          break
        case 'viewLogistics':
          this.handleViewLogistics(row)
          break
        case 'billDetails':
          this.handleBillDetails(row)
          break
        default:
          console.log('未知操作:', action)
      }
    },

    // 查看串码
    handleViewSerial(row) {
      console.log('查看串码:', row)
      this.imeiDialogData = { ...row }
      this.imeiDialogVisible = true
    },

    // 查看物流
    handleViewLogistics(row) {
      console.log('查看物流:', row)
      this.logisticsDialogData = { ...row }
      this.logisticsDialogVisible = true
    },

    // 串码弹窗确认
    handleConfirmImei() {
      this.imeiDialogVisible = false
    },

    // 串码弹窗取消
    handleCancelImei() {
      this.imeiDialogVisible = false
    },

    // 物流弹窗确认
    handleConfirmLogistics() {
      this.logisticsDialogVisible = false
    },

    // 物流弹窗取消
    handleCancelLogistics() {
      this.logisticsDialogVisible = false
    },

    // 账单详情
    handleBillDetails(row) {
      console.log('账单详情:', row)
      this.$message.info(`订单 ${row.noWarehouseOrderNumber} 的账单详情`)
    }
  }
}
</script>

<style lang="scss" scoped>
.all-order {
  padding: 20px;
  min-height: calc(100vh - 80px);
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.order-content {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.search-panel {
  margin-bottom: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;

  .el-form {
    .el-button {
      margin-right: 12px;
      border-radius: 6px;
      font-weight: 500;
      transition: all 0.3s ease;
    }

    .search-btn {
      background: linear-gradient(90deg, #5fb0ff, #4b8dff);
      border-color: #4b8dff;
      color: #fff;
      box-shadow: 0 6px 12px rgba(79, 148, 255, 0.35);

      &:hover,
      &:focus {
        background: linear-gradient(90deg, #6ac0ff, #5a9dff);
        border-color: #5a9dff;
        transform: translateY(-1px);
        box-shadow: 0 8px 16px rgba(79, 148, 255, 0.4);
      }
    }

    .el-button:not(.search-btn) {
      &:hover,
      &:focus {
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

.order-table-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e4e7ed;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;

.order-table {
    min-width: 100%;
    flex: 1;

    .el-table__header {
      th {
        background: #f7f8fa !important;
        color: #606266 !important;
        font-weight: 600 !important;
        font-size: 14px !important;
        border-bottom: 2px solid #e4e7ed !important;
      }
    }

    .el-table__body {
      tr {
        &:hover {
          background-color: #f5f7fa;
        }
      }

      td {
        border-bottom: 1px solid #ebeef5;
        padding: 8px 0;
      }
    }
  }

  .pagination-wrapper {
    padding: 16px 20px;
    background: #fff;
    border-top: 1px solid #e4e7ed;
    display: flex;
    justify-content: flex-end;
    flex-shrink: 0;

    .el-pagination {
      .el-pagination__total,
      .el-pagination__sizes,
      .el-pagination__jump {
        color: #606266;
        font-size: 14px;
      }

      .el-pager li {
        border-radius: 4px;
        margin: 0 2px;

        &.active {
          background: #409eff;
          color: #fff;
        }

        &:hover {
          color: #409eff;
        }
      }

      .btn-prev,
      .btn-next {
        border-radius: 4px;
        margin: 0 2px;

        &:hover {
          color: #409eff;
        }
      }
    }
  }
}

.no-data-content {
  text-align: center;
  padding: 40px 0;
  color: #909399;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
  }

  p {
    margin: 0;
      font-size: 16px;
  }
}

// 复制图标样式
.copy-icon {
  margin-left: 8px;
  color: #c0c4cc;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 14px;

  &:hover {
    color: #409eff;
  }
}

.price-text {
  color: #e6a23c;
  font-weight: bold;
}

.address-text {
  display: block;
  word-break: break-all;
  line-height: 1.4;
  max-width: 250px;
}

// 质量要求样式
.quality-requirements {
  .requirement-item {
    margin-bottom: 4px;
    font-size: 12px;
    color: #606266;
    line-height: 1.4;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

// 价格文本样式
.price-text {
  color: #f56c6c;
  font-weight: 500;
}
</style>
