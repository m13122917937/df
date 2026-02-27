<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="title"
    width="1500px"
    :close-on-click-modal="false"
    :append-to-body="true"
    :z-index="1900"
    @close="handleClose"
  >
    <div class="search-section">
      <div class="search-row">
        <!-- <div class="search-item">
          <span class="search-label">子类别</span>
          <el-input
            v-model.trim="searchForm.subCategory"
            placeholder="如 工资、餐饮、转账等"
            clearable
            class="search-input"
            @keyup.enter.native="handleSearch"
          />
        </div> -->
        <div class="search-item">
          <span class="search-label">交易对方</span>
          <el-input
            v-model.trim="searchForm.counterparty"
            placeholder="请输入交易对方"
            clearable
            class="search-input"
            @keyup.enter.native="handleSearch"
          />
        </div>
        <div class="search-item">
          <span class="search-label">交易日期</span>
          <el-date-picker
            style="width: 360px;"
            v-model="searchForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="datePickerOptions"
            class="search-input"
          />
        </div>
        <!-- <div class="search-item">
          <span class="search-label">类别</span>
          <el-select
            v-model="searchForm.category"
            placeholder="全部"
            clearable
            class="search-input"
          >
            <el-option :value="0" label="收入" />
            <el-option :value="1" label="支出" />
          </el-select>
        </div> -->
        <div class="search-actions">
          <el-button type="primary" size="mini" @click="handleSearch">搜索</el-button>
          <el-button size="mini" @click="handleReset">重置</el-button>
          <el-button size="mini" :loading="exportLoading" @click="handleExport"  icon="el-icon-download" type="success">导出</el-button>
        </div>
      </div>
    </div>

    <el-table
      :data="tableData"
      v-loading="loading"
      border
      style="width: 100%"
      height="400px"
      stripe
    >
      <template slot="empty">
        <EmptyState text="暂无流水记录" />
      </template>
      <el-table-column
        prop="category"
        label="类别"
        min-width="40"
      >
        <template slot-scope="scope">
          <span :style="{ color: scope.row.category === 0 ? '#67c23a' : '#f56c6c' }">
            {{ scope.row.category === 0 ? '收入' : '支出' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="subCategory"
        label="子类别"
        min-width="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="amount"
        label="交易金额"
        min-width="100"
      >
        <template slot-scope="scope">
          <span :style="{ color: scope.row.category === 0 ? '#67c23a' : '#f56c6c' }">
            {{ formatAmount(scope.row.amount) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column
        prop="balanceAfter"
        label="交易后余额"
        min-width="120"
      >
        <template slot-scope="scope">
          {{ formatAmount(scope.row.balanceAfter) }}
        </template>
      </el-table-column>
      <el-table-column
        prop="transactionDate"
        label="交易日期"
        min-width="120"
      />
      <el-table-column
        prop="counterparty"
        label="交易对方"
        min-width="160"
        show-overflow-tooltip
      />
      <el-table-column
        prop="paymentMethod"
        label="支付方式"
        min-width="70"
        show-overflow-tooltip
      />
      <el-table-column
        prop="remark"
        label="备注"
        width="200"
        show-overflow-tooltip
      />
      <el-table-column
        label="操作"
        min-width="150"
      >
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

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

    <!-- 编辑弹窗 -->
    <TransactionDialog
      :visible.sync="editDialogVisible"
      :payer-options="payerOptionsForEdit"
      :pay-name="accountName"
      :default-account-id="accountId"
      :edit-data="editRow"
      :append-to-body="true"
      :z-index="2100"
      @submit="handleEditSubmit"
      @cancel="handleEditCancel"
    />
  </el-dialog>
</template>

<script>
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue"
import { getTransactionListApi, exportTransactionListApi, deleteTransactionApi, updateTransactionApi } from "@/api/monery"
import TransactionDialog from "./TransactionDialog.vue"
import { saveAs } from "file-saver";
import { blobValidate } from "@/utils/ruoyi";

export default {
  name: "TransactionListDialog",
  components: { EmptyState, TransactionDialog },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    accountId: {
      type: [Number, String],
      default: null
    },
    accountName: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      tableData: [],
      searchForm: {
        subCategory: "",
        counterparty: "",
        category: "",
        dateRange: []
      },
      datePickerOptions: {
        disabledDate(time) {
          const end = new Date()
          end.setHours(23, 59, 59, 999)
          return time.getTime() > end.getTime()
        }
      },
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0
      },
      exportLoading: false,
      editDialogVisible: false,
      editRow: null
    }
  },
  computed: {
    title() {
      return this.accountName ? `${this.accountName} 流水记录` : "流水记录"
    },
    payerOptionsForEdit() {
      if (!this.accountId || !this.accountName) return []
      return [{ id: this.accountId, payName: this.accountName }]
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.pagination.currentPage = 1
        this.loadData()
      }
    },
    dialogVisible(val) {
      this.$emit("update:visible", val)
      if (!val) {
        // 关闭主弹窗时同步关闭子弹窗
        this.editDialogVisible = false
      }
    },
    accountId() {
      if (this.dialogVisible) {
        this.pagination.currentPage = 1
        this.loadData()
      }
    }
  },
  methods: {
    async loadData() {
      if (!this.accountId) return
      this.loading = true
      try {
        const params = {
          accountId: this.accountId,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          // subCategory: this.searchForm.subCategory || undefined, // 子类别 如 工资、餐饮、转账等
          // counterparty: this.searchForm.counterparty || undefined, // 交易对方
          // category: this.searchForm.category === "" ? undefined : this.searchForm.category, // 类别 0收入 1支出
          transactionDateStart: this.searchForm.dateRange && this.searchForm.dateRange.length === 2 ? this.searchForm.dateRange[0] : '',
          transactionDateEnd: this.searchForm.dateRange && this.searchForm.dateRange.length === 2 ? this.searchForm.dateRange[1] : ''
        }
        const res = await getTransactionListApi(params)
        if (res.code === 200) {
          this.tableData = res.rows || []
          this.pagination.total = res.total || 0
        } else {
          this.$message.error(res.msg || "获取流水记录失败")
        }
      } catch (error) {
        console.error("获取流水记录失败:", error)
        this.$message.error("获取流水记录失败")
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val
      this.loadData()
    },
    formatAmount(val) {
      const num = Number(val || 0)
      if (!isFinite(num)) return "0.00"
      return num.toLocaleString("zh-CN", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    handleClose() {
      this.tableData = []
      this.pagination = {
        currentPage: 1,
        pageSize: 30,
        total: 0
      }
      this.searchForm = {
        subCategory: "",
        counterparty: "",
        category: "",
        dateRange: []
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadData()
    },
    handleReset() {
      this.searchForm = {
        subCategory: "",
        counterparty: "",
        category: ""
      }
      this.pagination.currentPage = 1
      this.loadData()
    },
    async handleExport() {
      this.exportLoading = true
      const params = {
          accountId: this.accountId,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize,
          // subCategory: this.searchForm.subCategory || undefined, // 子类别 如 工资、餐饮、转账等
          // counterparty: this.searchForm.counterparty || undefined, // 交易对方
          // category: this.searchForm.category === "" ? undefined : this.searchForm.category, // 类别 0收入 1支出
          transactionDateStart: this.searchForm.dateRange && this.searchForm.dateRange.length === 2 ? this.searchForm.dateRange[0] : '',
          transactionDateEnd: this.searchForm.dateRange && this.searchForm.dateRange.length === 2 ? this.searchForm.dateRange[1] : ''
        }
        try {
          const res = await exportTransactionListApi(params)
          const isBlob = blobValidate(res);
        if (isBlob) {
          const blob = new Blob([res]);
          const supplierName = this.selectedSupplierName || '流水记录';
          const now = new Date();
          const timestamp =
            now.getFullYear() +
            String(now.getMonth() + 1).padStart(2, '0') +
            String(now.getDate()).padStart(2, '0') +
            '_' +
            String(now.getHours()).padStart(2, '0') +
            String(now.getMinutes()).padStart(2, '0')
          const fileName = `${supplierName}_${timestamp}.xlsx`;
          saveAs(blob, fileName);
          this.$message.success('导出成功');
        } else {
          const reader = new FileReader();
          reader.onload = (e) => {
            try {
              const result = JSON.parse(e.target.result || '{}');
              this.$message.error(result.msg || '导出失败');
            } catch (error) {
              this.$message.error('导出失败');
            }
          };
          reader.readAsText(res);
        }
        } catch (error) {
          console.error("导出失败:", error)
          this.$message.error("导出失败")
        } finally {
          this.exportLoading = false
        }
    },
    handleDelete(row) {
      this.$confirm('确认删除该流水记录吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            const res = await deleteTransactionApi(row.id)
            if (res && res.code === 200) {
              this.$message.success('删除成功')
              this.loadData()
              this.$emit('refresh-main')
            } else {
              this.$message.error(res?.msg || '删除失败')
            }
          } catch (error) {
            console.error('删除流水记录失败:', error)
            this.$message.error('删除失败，请稍后重试')
          }
        })
        .catch(() => {})
    },
    handleEdit(row) {
      this.editRow = { ...row }
      this.editDialogVisible = true
    },
    async handleEditSubmit(form) {
      try {
        const payload = { ...form, id: this.editRow?.id, accountId: this.accountId }
        const res = await updateTransactionApi(payload)
        if (res && res.code === 200) {
          this.$message.success('保存成功')
          this.editDialogVisible = false
          this.loadData()
          this.$emit('refresh-main')
        } else {
          this.$message.error(res?.msg || '保存失败')
        }
      } catch (error) {
        console.error('保存流水记录失败:', error)
        this.$message.error('保存失败，请稍后重试')
      }
    },
    handleEditCancel() {
      this.editDialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-section {
  margin-bottom: 10px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.search-label {
  font-size: 13px;
  color: #606266;
  white-space: nowrap;
}

.search-input {
  width: 180px;
}

.search-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.pagination-section {
  margin-top: 12px;
  text-align: right;
}
</style>


