<template>
  <div class="payment-configuration-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <div class="search-container">
          <div class="search-row">
            <div class="search-item">
              <label class="search-label">店铺名称</label>
              <el-input
                v-model.trim="searchForm.keyword"
                placeholder="请输入店铺名称"
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
          @click="handleAddConfig"
        >
          新增配置
        </el-button>
      </div>
    </div>

    <!-- 数据表格区域 -->
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
          <EmptyState text="暂无数据" />
        </template>
        <el-table-column
          prop="keyWord"
          label="店铺名称"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="payerName"
          label="付款主体"
          min-width="250"
          show-overflow-tooltip
        />
        <el-table-column
          prop="platform"
          min-width="120"
          label="平台"
          show-overflow-tooltip
        />
        <el-table-column
          prop="createBy"
          min-width="120"
          label="创建人"
          show-overflow-tooltip
        />
        <el-table-column
          prop="createTime"
          label="创建时间"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column
          prop="updateByName"
          min-width="120"
          label="修改人"
          show-overflow-tooltip
        />
        <el-table-column
          prop="updateTime"
          label="修改时间"
          min-width="200"
          show-overflow-tooltip
        />
        <el-table-column label="操作" fixed="right" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEdit(scope.row)"
            >
              编辑
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

    <!-- 新增/编辑配置对话框 -->
    <PaymentConfigDialog
      :visible.sync="dialogVisible"
      :is-edit="isEdit"
      :form-data="form"
      :shop-name-options="keyWordOptions"
      :payer-name-options="payerNameOptions"
      :platform-options="platformOptions"
      @submit="handleSubmit"
    />
  </div>
</template>

<script>
import PaymentConfigDialog from './components/PaymentConfigDialog.vue'
import EmptyState from "@/views/demandManage/wholesale/components/emptyState.vue";
import { getPayerConfigListApi, addPayerConfigApi, updatePayerConfigApi, getPayerAllListApi,getPayerConfigPlatformListApi } from "@/api/monery"

export default {
  name: 'PaymentConfiguration',
  components: {
    PaymentConfigDialog,
    EmptyState
  },
  data() {
    return {
      loading: false,
      searchForm: {
        keyword: ''
      },
      tableData: [],
      pagination: {
        currentPage: 1,
        pageSize: 30,
        total: 0
      },
      keyWordOptions: [],
      payerNameOptions: [],
      platformOptions: [],
      dialogVisible: false,
      isEdit: false,
      currentId: null,
      form: {
        keyWord: '',
      }
    }
  },
  created() {
    this.loadTableData()
    this.getPayerAllListOptions()
    this.getPayerConfigPlatformListOptions()
  },
  methods: {
    // 获取收款主体下拉列表
    async getPayerAllListOptions() {
      const res = await getPayerAllListApi({})
      const { code, data = [] } = res;
      if (code != 200) {
        return;
      }
      this.payerNameOptions = data.map(item => ({
        label: `${item.payName}(${item.bankName})`,
        value: item.id
      })) || [];
    },
    // 获取平台下拉列表
    async getPayerConfigPlatformListOptions() {
      const res = await getPayerConfigPlatformListApi({})
      const { code, data = [] } = res;
      if (code != 200) {
        return;
      }
      this.platformOptions = data.map(item => ({
        label: item,
        value: item
      })) || [];
    },
    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    // 重置
    handleReset() {
      this.searchForm = {
        keyword: ''
      }
      this.pagination.currentPage = 1
      this.loadTableData()
    },
    // 新增配置
    handleAddConfig() {
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
        keyWord: row.keyWord,
        payerId: row.payerId,
        platform: row.platform,
        id: row.id
      }
      this.dialogVisible = true
    },
    // 加载表格数据
    async loadTableData() {
      this.loading = true
      try {
        // TODO: 替换为实际的 API 调用
        const params = {
          ...this.searchForm,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getPayerConfigListApi(params)
        if (res.code === 200) {
          this.tableData = res.rows || [];
          this.pagination.total = res.total || 0;
        }
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
          const res = await updatePayerConfigApi(formData)
          if (res.code === 200) {
            this.$message.success('修改成功')
            this.dialogVisible = false
            this.resetForm()
            this.loadTableData()
          }
        } else {
          const res = await addPayerConfigApi(formData)
          if (res.code === 200) {
            this.$message.success('添加成功')
            this.dialogVisible = false
            this.resetForm()
            this.loadTableData()
          }
        }   
      } catch (error) {
        console.error(isEdit ? '修改失败' : '添加失败:', error)
        const errorMessage = isEdit ? '修改失败' : '添加失败'
        this.$message.error(errorMessage)
      }
    },
    // 重置表单
    resetForm() {
      this.form = {
        keyWord: '',
        payerId: '',
        platform: ''
      }
    }
  }
}
</script>

<style scoped lang="scss">
.payment-configuration-container {
  padding: var(--page-padding);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);

  .search-section {
    margin-bottom: 20px;
    padding: 12px 16px;
    background: var(--bg-card);
    border-radius: var(--radius);
    border: 1px solid var(--border-tags);
    box-shadow: var(--shadow-card);
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

  .search-input {
    width: 400px;
  }

  .search-input :deep(.el-input__inner) {
    border-radius: 8px;
    font-size: 14px;
    padding: 12px 16px 12px 40px;
    transition: all 0.3s ease;
    border: 2px solid #e4e7ed;
    background: var(--bg-card);
    box-shadow: var(--shadow-card);
  }

  .search-input :deep(.el-input__inner):focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
    background: var(--bg-hover);
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
    background: var(--bg-card);
    padding: 24px;
    border-radius: var(--radius);
    box-shadow: var(--shadow-card);
    flex: 1;
    overflow: auto;

    .el-table {
      border-radius: 6px;
      overflow: hidden;

      th {
        background-color: var(--bg-table-header);
        color: #303133;
        font-weight: 600;
        border-bottom: 1px solid #ebeef5;
      }

      td {
        border-bottom: 1px solid #f0f0f0;
      }

      .el-table__row:hover {
        background-color: var(--bg-page);
      }
    }
  }

  .pagination-section {
    margin-top: 20px;
    text-align: right;
    padding: 12px 24px;
    background: var(--bg-card);
    border-radius: var(--radius);
    box-shadow: var(--shadow-card);
  }
}
</style>
