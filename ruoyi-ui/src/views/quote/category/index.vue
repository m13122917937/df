<template>
  <div class="app-container quote-category-page">
    <el-card shadow="never" class="quote-category-card">
      <div slot="header" class="quote-category-card-header">
        <span>品类管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增品类</el-button>
      </div>

      <el-table v-loading="loading" :data="categoryList" border stripe size="medium">
        <el-table-column label="图片" width="100" align="center">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              fit="cover"
              style="width: 48px; height: 48px; border-radius: 6px"
            />
            <span v-else class="quote-category-empty-img">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="品类名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="120" align="center" />
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" class="quote-danger-text" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog :title="form.id ? '编辑品类' : '新增品类'" :visible.sync="dialogVisible" width="520px" append-to-body>
      <el-form ref="categoryForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="品类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入品类名称" maxlength="128" />
        </el-form-item>
        <el-form-item label="品类图片">
          <image-upload v-model="form.imageUrl" :limit="1" action="/common/v2/upload" />
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
import { getQuoteCategoryPage, saveQuoteCategory, delQuoteCategory } from '@/api/quote'

export default {
  name: 'QuoteCategory',
  data() {
    return {
      loading: false,
      total: 0,
      categoryList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      form: {
        id: undefined,
        categoryName: '',
        imageUrl: '',
        sortOrder: 0
      },
      rules: {
        categoryName: [
          { required: true, message: '品类名称不能为空', trigger: 'blur' }
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
      getQuoteCategoryPage({ ...this.queryParams }).then((response) => {
        this.categoryList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleAdd() {
      this.form = { id: undefined, categoryName: '', imageUrl: '', sortOrder: 0 }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.categoryForm && this.$refs.categoryForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.form = {
        id: row.id,
        categoryName: row.categoryName,
        imageUrl: row.imageUrl,
        sortOrder: row.sortOrder
      }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确认删除品类“${row.categoryName}”吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delQuoteCategory(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {
      })
    },
    handleSubmit() {
      this.$refs.categoryForm.validate((valid) => {
        if (!valid) {
          return
        }
        saveQuoteCategory(this.form).then(() => {
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
.quote-category-page {
  .quote-category-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-category-empty-img {
    color: #999999;
    font-size: 12px;
  }

  .quote-danger-text {
    color: #f56c6c;
  }
}
</style>
