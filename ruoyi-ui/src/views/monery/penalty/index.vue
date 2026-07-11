<template>
  <div class="penalty-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <div class="search-container">
          <div class="search-row">
            <div class="search-item">
              <label class="search-label">供应商</label>
              <el-select
                v-model="logisticsSearchForm.companyId"
                placeholder="请输入供应商名称"
                clearable
                filterable
                remote
                reserve-keyword
                :remote-method="handleSupplierRemote"
                :loading="supplierLoading"
                @visible-change="handleSupplierVisible"
                class="search-select"
              >
                <el-option
                  v-for="item in supplierOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </div>
            <div class="search-item">
              <label class="search-label">订单编号</label>
              <el-input
                v-model="orderCodeInput"
                @keyup.enter.native="handleLogisticsSearch"
                placeholder="请输入订单编号，多个用逗号分隔"
                class="search-input"
                clearable
              />
            </div>
            <div class="search-item">
              <label class="search-label">商家单号</label>
              <el-input
                v-model="originalOrderIdInput"
                @keyup.enter.native="handleLogisticsSearch"
                placeholder="请输入商家单号，多个用逗号分隔"
                class="search-input"
                clearable
              />
            </div>
          </div>
        </div>
        <div class="search-actions">
          <el-button
            icon="el-icon-refresh"
            @click="handleLogisticsReset"
            class="reset-btn"
          >
            重置
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-search"
            v-throttle-click="600"
            @click="handleLogisticsSearch"
            class="search-action-btn"
          >
            搜索
          </el-button>
        </div>
      </div>
      <div class="toolbar-slot">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="handleAddLogisticsPenalty"
        >
          添加扣罚订单
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        :header-cell-style="{
          background: '#f7f8fa',
          color: '#606266',
          fontWeight: 600,
        }"
        height="100%"
        :cell-style="{ padding: '8px 0' }"
        :data="logisticsTableData"
        v-loading="logisticsLoading"
        border
        style="width: 100%;"
        stripe
      >
        <template slot="empty">
          <EmptyState text="暂无扣罚数据" />
        </template>
        <el-table-column label="单号" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="combined-info">
              <div class="info-item">
                <span class="info-label">内部：</span>
                <span class="info-value">{{ scope.row.orderCode || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">旺店通id：</span>
                <span class="info-value">{{ scope.row.erpId || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">商家：</span>
                <span class="info-value">{{ scope.row.originalOrderId || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="产品信息" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">
            <div class="product-info">
              <div class="info-item">
                <span class="info-label">品牌/品类：</span>
                <span class="info-value">
                  {{ (scope.row.brand && scope.row.category) ? `${scope.row.brand}/${scope.row.category}` : (scope.row.brand || scope.row.category || '-') }}
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">商品：</span>
                <span class="info-value">{{ scope.row.spuName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">SKU：</span>
                <span class="info-value">{{ scope.row.skuName || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="companyName" label="供应商" min-width="200" show-overflow-tooltip />
        <el-table-column prop="remark" label="扣罚原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sendTime" label="发货时间" min-width="200">
          <template slot-scope="scope">
            <span>{{ formatDateTime(scope.row.sendTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="tradePrice" label="订单金额" min-width="200">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.tradePrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="扣罚金额" min-width="200">
          <template slot-scope="scope">
            <span class="primary-color">{{ formatAmount(scope.row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              class="primary-color-text"
              @click="handleRevoke(scope.row)"
            >
              撤销
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleLogisticsSizeChange"
        @current-change="handleLogisticsCurrentChange"
        :current-page="logisticsPagination.currentPage"
        :page-sizes="[30, 50, 100]"
        :page-size="logisticsPagination.pageSize"
        :total="logisticsPagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 添加扣罚订单对话框 -->
    <AddPenaltyOrderDialog
      :visible.sync="addPenaltyDialogVisible"
      @submit="handleAddPenaltySubmit"
    />
  </div>
</template>

<script>
import AddPenaltyOrderDialog from './components/AddPenaltyOrderDialog.vue'
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { getDeductionListApi, getDeductionSaveApi,getDeductionApi,getDeductionRevokeApi } from "@/api/monery";
import { getBusinessCompanyListApi } from "@/api/business";

export default {
  name: 'Penalty',
  components: {
    AddPenaltyOrderDialog,
    EmptyState
  },
  data() {
    return {
      // 物流扣罚相关
      logisticsSearchForm: {
        companyId: null,
        orderCodeList: [],
        originalOrderIdList: []
      },
      // 输入框字段（用于处理逗号分隔的字符串）
      orderCodeInput: '',
      originalOrderIdInput: '',
      logisticsTableData: [],
      logisticsLoading: false,
      logisticsPagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0
      },
      // 合计金额
      totalAmount: 0,
      // 选项数据
      supplierOptions: [],
      supplierLoading: false,
      // 添加扣罚订单对话框
      addPenaltyDialogVisible: false
    }
  },
  created() {
    this.handleSupplierRemote()
    this.loadLogisticsData()
  },
  methods: {
    // 将逗号分隔的字符串转换为数组
    parseStringToArray(str) {
      if (!str || !str.trim()) return []
      return str.split(',').map(item => item.trim()).filter(item => item)
    },
    // 物流扣罚搜索
    handleLogisticsSearch() {
      // 将输入框的字符串转换为数组
      this.logisticsSearchForm.orderCodeList = this.parseStringToArray(this.orderCodeInput)
      this.logisticsSearchForm.originalOrderIdList = this.parseStringToArray(this.originalOrderIdInput)
      this.logisticsPagination.currentPage = 1
      this.loadLogisticsData()
    },
    // 物流扣罚重置
    handleLogisticsReset() {
      this.logisticsSearchForm = {
        companyId: null,
        orderCodeList: [],
        originalOrderIdList: []
      }
      this.orderCodeInput = ''
      this.originalOrderIdInput = ''
      this.logisticsPagination.currentPage = 1
      this.loadLogisticsData()
    },
    // 导出付款详情
    handleExportDetails() {
      this.$message.info('导出付款详情功能待实现')
    },
    // 添加物流扣罚订单
    handleAddLogisticsPenalty() {
      this.addPenaltyDialogVisible = true
    },
    // 提交添加扣罚订单
    async handleAddPenaltySubmit(formData) {
      try {
        const res = await getDeductionSaveApi(formData)
        if (res.code === 200) {
          this.$message.success('添加成功')
          this.addPenaltyDialogVisible = false
          this.loadLogisticsData()
        } else {
          this.$message.error(res.msg || '添加失败')
        }
      } catch (error) {
        console.error('添加失败:', error)
        this.$message.error(error.msg || '添加失败')
      }
    },
    // 查看
    handleView(row) {
      this.$message.info('查看功能待实现')
    },
    // 撤销扣罚
    handleRevoke(row) {
      this.$confirm('确定要撤销该扣罚订单吗？撤销后无法恢复。', '撤销确认', {
        confirmButtonText: '确定撤销',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: false
      }).then(async () => {
        try {
          if (!row.orderCode) {
            this.$message.error('订单编号不存在，无法撤销')
            return
          }
          const res = await getDeductionRevokeApi(row.orderCode)
          if (res.code === 200) {
            this.$message.success('撤销成功')
            this.loadLogisticsData()
          } else {
            this.$message.error(res.msg || '撤销失败')
          }
        } catch (error) {
          console.error('撤销失败:', error)
          this.$message.error(error.msg || '撤销失败')
        }
      }).catch(() => {
        // 用户取消操作
      })
    },
    // 加载物流扣罚数据
    async loadLogisticsData() {
      this.logisticsLoading = true
      try {
        // 构建请求参数（包含分页参数）
        const params = {
        
          companyId: this.logisticsSearchForm.companyId || null,
          orderCodeList: this.logisticsSearchForm.orderCodeList || [],
          originalOrderIdList: this.logisticsSearchForm.originalOrderIdList || []
        }
        const pageData = {
          pageNum: this.logisticsPagination.currentPage,
          pageSize: this.logisticsPagination.pageSize
        }
        const res = await getDeductionListApi(params,pageData)
        if (res.code === 200) {
          // 处理分页响应：可能是 res.data.records 或 res.rows 或 res.data
          let dataList = []
          if (res.data && Array.isArray(res.data)) {
            dataList = res.data
            this.logisticsPagination.total = res.total || res.data.length
          } else if (res.data && res.data.records) {
            dataList = res.data.records || []
            this.logisticsPagination.total = res.data.total || 0
          } else if (res.rows) {
            dataList = res.rows || []
            this.logisticsPagination.total = res.total || 0
          } else {
            dataList = []
            this.logisticsPagination.total = 0
          }
          
          this.logisticsTableData = dataList
          // 计算合计金额（扣罚金额）
          this.totalAmount = this.logisticsTableData.reduce((sum, item) => {
            return sum + (Number(item.amount) || 0)
          }, 0)
        } else {
          this.$message.error(res.msg || '加载数据失败')
          this.logisticsTableData = []
          this.logisticsPagination.total = 0
          this.totalAmount = 0
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
        this.logisticsTableData = []
        this.logisticsPagination.total = 0
        this.totalAmount = 0
      } finally {
        this.logisticsLoading = false
      }
    },
    // 物流扣罚分页大小改变
    handleLogisticsSizeChange(val) {
      this.logisticsPagination.pageSize = val
      this.logisticsPagination.currentPage = 1
      this.loadLogisticsData()
    },
    // 物流扣罚当前页改变
    handleLogisticsCurrentChange(val) {
      this.logisticsPagination.currentPage = val
      this.loadLogisticsData()
    },
    // 格式化金额
    formatAmount(amount) {
      if (!amount && amount !== 0) return '0.00'
      return Number(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      if (isNaN(date.getTime())) return dateTime
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    // 供应商远程搜索
    async handleSupplierRemote(keyword = '') {
      const trimmed = (keyword || '').trim();
      this.supplierLoading = true;
      try {
        const pageData = {
          pageNum: 1,
          pageSize: 30
        };
        const params = {}
        if (trimmed) {
          params.companyNameLike = trimmed;
        }
        const res = await getBusinessCompanyListApi(params,pageData);
        const list = res?.rows || res?.data || [];
        this.supplierOptions = Array.isArray(list) ? list.map(item => ({
          label: item.companyName,
          value: item.id,
          raw: item
        })) : [];
      } catch (error) {
        console.error('获取供应商列表失败', error);
        this.supplierOptions = [];
      } finally {
        this.supplierLoading = false;
      }
    },
    // 供应商下拉框显示/隐藏
    handleSupplierVisible(visible) {
      if (visible && !this.supplierOptions.length) {
        this.handleSupplierRemote();
      }
    }
  }
}
</script>

<style scoped lang="scss">
.penalty-container {
  height: calc(100vh - 100px);
  padding: 20px;
  display: flex;
  flex-direction: column;

  // 搜索区域
  .search-section {
    margin-bottom: 20px;
    padding: 10px;
    background: var(--bg-card);
    border-radius: 12px;
    border: 1px solid var(--border-tags);
    box-shadow: var(--shadow-card);
    position: relative;
    overflow: hidden;
  }

  .search-section::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c);
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
    min-width: 100%;
    gap: 12px;
  }

  .search-item {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .search-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--adm-text-secondary);
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
    min-width: 4px;
    height: 4px;
    background: #409eff;
    border-radius: 50%;
  }

  .search-select,
  .search-input {
    min-width: 220px;
  }

  .search-select :deep(.el-input__inner),
  .search-input :deep(.el-input__inner) {
    border-radius: 8px;
    font-size: 14px;
    padding: 12px 16px;
    transition: all 0.3s ease;
    border: 2px solid var(--border-tags);
    background: var(--bg-card);
  }

  .search-select :deep(.el-input__inner):focus,
  .search-input :deep(.el-input__inner):focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
    background: var(--bg-hover);
  }

  .search-actions {
    display: flex;
    min-width: 200px;
    justify-content: space-between;
    align-items: center;
  }

  .toolbar-slot {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid var(--border-tags);
    display: flex;
    justify-content: flex-end;
    align-items: center;
    min-height: 40px;
    gap: 12px;
  }

  .search-action-btn {
    height: 36px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
    transition: all 0.3s ease;
    min-width: 100px;
  }

  .search-action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }

  .reset-btn {
    height: 36px;
    font-size: 14px;
    border-radius: 8px;
    color: var(--adm-text-secondary);
    border-color: #dcdfe6;
    transition: all 0.3s ease;
    min-width: 100px;
  }

  .reset-btn:hover {
    color: #409eff;
    border-color: #409eff;
    background: var(--bg-hover);
  }

  // 导出按钮区域
  .export-section {
    margin-bottom: 20px;
  }

  // 表格区域
  .table-section {
    background: var(--bg-card);
    padding: 24px;
    border-radius: 6px;
    box-shadow: var(--shadow-card);
    border: 1px solid var(--border-tags);
    flex: 1;
    overflow: auto;

     .el-table {
      border-radius: 6px;
      overflow: hidden;

      th {
        background-color: var(--bg-table-header);
        color: var(--adm-text-primary);
        font-weight: 600;
        border-bottom: 1px solid var(--border-tags);
      }

      td {
        border-bottom: 1px solid var(--border-tags);
      }

      .el-table__row:hover {
        background-color: var(--bg-hover);
      }
    }
  }

  // 分页区域
  .pagination-section {
    margin-top: 20px;
    text-align: right;
    padding: 20px;
    background: var(--bg-card);
    border-radius: 6px;
    box-shadow: var(--shadow-card);
    border: 1px solid var(--border-tags);
  }

  // 主题色相关类
  .primary-color {
    color: var(--color-primary);
  }

  .primary-color-text {
    color: var(--color-primary) !important;
  }

  // 合并信息列样式
  .combined-info {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 4px 0;

    .info-item {
      display: flex;
      align-items: center;
      font-size: 13px;
      line-height: 1.5;

      .info-label {
        color: var(--adm-text-tertiary);
        font-weight: 500;
        margin-right: 4px;
        flex-shrink: 0;
      }

      .info-value {
        color: var(--adm-text-secondary);
        word-break: break-all;
      }
    }
  }

  // 产品信息列样式
  .product-info {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 4px 0;

    .info-item {
      display: flex;
      align-items: center;
      font-size: 13px;
      line-height: 1.5;

      .info-label {
        color: var(--adm-text-tertiary);
        font-weight: 500;
        margin-right: 4px;
        flex-shrink: 0;
      }

      .info-value {
        color: var(--adm-text-secondary);
        word-break: break-all;
      }
    }
  }
}
</style>
