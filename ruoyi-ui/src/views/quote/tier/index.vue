<template>
  <div class="app-container quote-tier-page">
    <el-card shadow="never" class="quote-tier-card">
      <div slot="header" class="quote-tier-card-header">
        <span>价格档位管理</span>
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          v-hasPermi="['quote:tier:list']"
          @click="handleAdd"
        >新增档位</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tierList"
        border
        stripe
        size="medium"
      >
        <el-table-column prop="tierName" label="档位名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="120" align="center" />
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              type="text"
              icon="el-icon-delete"
              class="quote-danger-text"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog
      :title="form.id ? '编辑档位' : '新增档位'"
      :visible.sync="dialogVisible"
      width="480px"
      append-to-body
    >
      <el-form ref="tierForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="档位名称" prop="tierName">
          <el-input v-model="form.tierName" placeholder="请输入档位名称，如：批发价" maxlength="64" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getQuoteTierPage, saveQuoteTier, delQuoteTier } from '@/api/quote'

export default {
  name: 'QuoteTier',
  data() {
    return {
      loading: false,
      total: 0,
      tierList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      form: {
        id: undefined,
        tierName: '',
        sortOrder: 0
      },
      rules: {
        tierName: [
          { required: true, message: '档位名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getQuoteTierPage({
        ...this.queryParams
      }).then((response) => {
        this.tierList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleAdd() {
      this.form = {
        id: undefined,
        tierName: '',
        sortOrder: 0
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.tierForm && this.$refs.tierForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.form = {
        id: row.id,
        tierName: row.tierName,
        sortOrder: row.sortOrder
      }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确认删除档位“${row.tierName}”吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delQuoteTier(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {
      })
    },
    handleSubmit() {
      this.$refs.tierForm.validate((valid) => {
        if (!valid) {
          return
        }
        saveQuoteTier(this.form).then(() => {
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.getList()
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-tier-page {
  .quote-tier-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-danger-text {
    color: #f56c6c;
  }
}
</style>
