<template>
  <div class="payment-entity-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <div class="search-container">
          <div class="search-row">
            <div class="search-item">
              <label class="search-label">收款企业名称</label>
              <el-input
                v-model="searchForm.payName"
                placeholder="请输入收款企业名称"
                clearable
                class="search-input"
                prefix-icon="el-icon-search"
                @keyup.enter.native="handleSearch"
              />
            </div>
          </div>
        </div>
        <div class="search-actions">
          <el-button
            icon="el-icon-refresh"
            @click="handleReset"
            class="reset-btn"
          >
            重置
          </el-button>
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleSearch"
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
          @click="handleAdd"
        >
          添加收款主体
        </el-button>
      </div>
    </div>

    <!-- 主体表格 -->
    <div class="table-section">
      <el-table
        :header-cell-style="{
          background: '#f7f8fa',
          color: '#606266',
          fontWeight: 600,
        }"
        :cell-style="{ padding: '8px 0' }"
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
        height="100%"
        stripe
      >
      <template slot="empty">
          <EmptyState text="暂无企业数据" />
        </template>
        <el-table-column
          prop="payName"
          label="收款企业名称"
          min-width="250"
          show-overflow-tooltip
        />
        <el-table-column
          prop="nickName"
          label="简称"
          min-width="80"
          show-overflow-tooltip
        />
        <el-table-column
          prop="outCode"
          label="吉客云编号"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="bankName"
          label="开户行全称"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="payNo"
          label="银行账号"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="balance"
          label="账户余额"
          min-width="180"
        >
          <template slot-scope="scope">
            <!-- <el-tooltip content="点击查看该账户的流水记录" placement="top"> -->
              <span
                class="balance-clickable"
                @click="openTransactionList(scope.row)"
              >
                <i class="el-icon-document balance-icon" />
                <span class="balance-amount">
                  {{ formatBalance(scope.row.balance) }}
                </span>
              </span>
            <!-- </el-tooltip> -->
          </template>
        </el-table-column>
        <el-table-column
          prop="actived"
          label="状态"
          min-width="100"
        >
          <template slot-scope="scope">
            <el-tag :type="scope.row.actived === 0 ? 'success' : 'info'" size="small">
              {{ scope.row.actived === 0 ? '激活' : '弃用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createName"
          label="创建人"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="200"
          show-overflow-tooltip
        />
         <el-table-column
          prop="updateName"
          label="修改人"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column label="操作" fixed="right" width="180">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="success"
              style="margin-left: 8px;"
              :disabled="scope.row.actived !== 0"
              @click="handleAddTransaction(scope.row)"
            >
              新增流水
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[30, 50, 100]"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>
    <PaymentEntityDialog
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :form-data="form"
      :company-options="payerOptions"
      @submit="handleSubmit"
    />

    <TransactionDialog
      :visible.sync="transactionDialogVisible"
      :payer-options="payerOptions"
      :pay-name="defaultAccountName"
      :default-account-id="defaultAccountId"
      @submit="handleTransactionSubmit"
    />

    <TransactionListDialog
      :visible.sync="transactionListVisible"
      :account-id="transactionListAccount.id"
      :account-name="transactionListAccount.name"
      @refresh-main="loadTableData"
    />
  </div>
</template>

<script>
import PaymentEntityDialog from './components/PaymentEntityDialog.vue'
import TransactionDialog from './components/TransactionDialog.vue'
import TransactionListDialog from './components/TransactionListDialog.vue'
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue"
import { getPayerListApi, addPayerApi, updatePayerApi, getPayerAllListApi, addTransactionApi } from "@/api/monery"

export default {
  name: 'MainConfiguration',
  components: {
    PaymentEntityDialog,
    TransactionDialog,
    TransactionListDialog,
    EmptyState
  },
  data() {
    return {
      loading: false,
      searchForm: {
        payName: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      dialogVisible: false,
      isEdit: false,
      currentId: null,
      form: {
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      },
      payerOptions: [],
      defaultAccountId: null,
      transactionDialogVisible: false,
      transactionListVisible: false,
      transactionListAccount: {
        id: null,
        name: ''
      },
      defaultAccountName: null
    }
  },
  created() {
    this.loadTableData()
    this.fetchAllPayers()
  },
  methods: {
    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    // 重置
    handleReset() {
      this.searchForm = {
        payName: ''
      }
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    // 添加
    handleAdd() {
      this.isEdit = false
      this.currentId = null
      this.resetForm()
      this.dialogVisible = true
    },
    // 编辑
    handleEdit(row) {
      this.isEdit = true
      this.currentId = row.id
      this.form = {
        payName: row.payName,
        nickName: row.nickName || '',
        outCode: row.outCode || '',
        bankName: row.bankName,
        payNo: row.payNo,
        balance: row.balance || 0,
        actived: typeof row.actived === 'number' ? row.actived : 0,
        id: row.id
      }
      this.dialogVisible = true
    },
    // 加载表格数据
    async loadTableData() {
      this.loading = true
      try {
        const params = {
          ...this.searchForm,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getPayerListApi(params)
        const { code, rows = [], total = 0 } = res
        if (code !== 200) return
        this.tableData = rows || []
        this.pagination.total = total || 0
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    // 分页大小改变
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    // 当前页改变
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadTableData()
    },
    // 表单提交
    async handleSubmit({ isEdit, formData }) {
      try {
        if (isEdit) {
          const res = await updatePayerApi(formData)
          if (res.code === 200) {
            this.$message.success('修改成功')
            this.dialogVisible = false
            this.resetForm()
            this.loadTableData()
            this.fetchAllPayers()
          }
        } else {
          const res = await addPayerApi(formData)
          if (res.code === 200) {
            this.$message.success('添加成功')
            this.dialogVisible = false
            this.resetForm()
            this.loadTableData()
            this.fetchAllPayers()
          }
        }
      } catch (error) {
        console.error(isEdit ? '修改失败' : '添加失败:', error)
        this.$message.error(isEdit ? '修改失败' : '添加失败')
      }
    },
    formatBalance(val) {
      const num = Number(val || 0)
      if (!isFinite(num)) return '0.00'
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },
    // 重置表单
    resetForm() {
      this.form = {
        payName: '',
        nickName: '',
        outCode: '',
        bankName: '',
        payNo: '',
        balance: 0,
        actived: 0
      }
    },
    async fetchAllPayers() {
      try {
        const res = await getPayerAllListApi()
        if (res.code === 200) {
          this.payerOptions = Array.isArray(res.data) ? res.data : []
        }
      } catch (error) {
        console.error('获取收款主体列表失败:', error)
      }
    },
    /* 流水记录：新增弹框 */
    handleAddTransaction(row) {
      this.defaultAccountId = row.id
      this.defaultAccountName = row.payName
      this.transactionDialogVisible = true
    },
    async handleTransactionSubmit(formData) {
      try {
        const res = await addTransactionApi(formData)
        if (res.code === 200) {
          this.$message.success('新增流水记录成功')
          this.loadTableData()
          this.transactionDialogVisible = false
        }
      } catch (error) {
        console.error('新增流水记录失败:', error)
      }
    },
    // 余额弹窗流水列表
    openTransactionList(row) {
      if (!row || !row.id) return
      this.transactionListAccount = {
        id: row.id,
        name: row.payName
      }
      this.transactionListVisible = true
    }
  }
}
</script>

<style scoped lang="scss">
.payment-entity-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);

  .search-section {
    margin-bottom: 20px;
    padding: 10px;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border-radius: 12px;
    border: 1px solid #e9ecef;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
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
    width: 100%;
    gap: 6px;
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

  .search-input {
    width: 400px;
  }

  .search-input :deep(.el-input__inner) {
    border-radius: 8px;
    font-size: 14px;
    padding: 12px 16px 12px 40px;
    transition: all 0.3s ease;
    border: 2px solid #e4e7ed;
    background: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  }

  .search-input :deep(.el-input__inner):focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
    background: #fafbfc;
  }

  .search-input :deep(.el-input__prefix) {
    left: 12px;
    color: #909399;
  }

  .search-actions {
    display: flex;
    width: 200px;
    justify-content: space-between;
    align-items: center;
  }

  .toolbar-slot {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #e4e7ed;
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
    width: 100px;
  }

  .search-action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }

  .reset-btn {
    height: 36px;
    font-size: 14px;
    border-radius: 8px;
    color: #606266;
    border-color: #dcdfe6;
    transition: all 0.3s ease;
    width: 100px;
  }

  .reset-btn:hover {
    color: #409eff;
    border-color: #409eff;
    background: #f0f9ff;
  }

  .table-section {
    background: #fff;
    padding: 24px;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 1px solid #f0f0f0;
    flex: 1;
    overflow: auto;

    .el-table {
      border-radius: 6px;
      overflow: hidden;

      th {
        background-color: #fafafa;
        color: #303133;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
      }

      td {
        border-bottom: 1px solid #f0f0f0;
      }

      .el-table__row:hover {
        background-color: #f5f7fa;
      }
    }
  }

  .balance-clickable {
    display: inline-flex;
    align-items: center;
    cursor: pointer;
    color: #409eff;
    font-weight: 500;
  }

  .balance-clickable:hover {
    color: #66b1ff;
  }

  .balance-icon {
    font-size: 14px;
    margin-right: 4px;
  }

  .balance-amount {
    text-decoration: underline dotted;
  }

  .pagination-section {
    margin-top: 20px;
    text-align: right;
    padding: 20px;
    background: #fff;
    border-radius: 6px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 1px solid #f0f0f0;
  }
}
</style>
