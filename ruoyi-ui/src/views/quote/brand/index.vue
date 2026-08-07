<template>
  <div class="app-container quote-brand-page">
    <el-card shadow="never" class="quote-brand-card">
      <div slot="header" class="quote-brand-card-header">
        <span>品牌管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增品牌</el-button>
      </div>

      <el-table v-loading="loading" :data="brandList" border stripe size="medium">
        <el-table-column label="图片" width="100" align="center">
          <template slot-scope="scope">
            <el-image
              v-if="scope.row.imageUrl"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              fit="cover"
              style="width: 48px; height: 48px; border-radius: 6px"
            />
            <span v-else class="quote-brand-empty-img">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="brandName" label="品牌名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="120" align="center" />
        <el-table-column prop="updateBy" label="更新人" min-width="120" align="center" />
        <el-table-column prop="updateTime" label="更新时间" min-width="180" align="center" />
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

    <el-dialog :title="form.id ? '编辑品牌' : '新增品牌'" :visible.sync="dialogVisible" width="520px" append-to-body>
      <el-form ref="brandForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="form.brandName" placeholder="请输入品牌名称" maxlength="128" />
        </el-form-item>
        <el-form-item label="品牌图片">
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
import { getQuoteBrandPage, saveQuoteBrand, delQuoteBrand } from '@/api/quote'

export default {
  name: 'QuoteBrand',
  data() {
    return {
      loading: false,
      total: 0,
      brandList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20
      },
      dialogVisible: false,
      form: {
        id: undefined,
        brandName: '',
        imageUrl: '',
        sortOrder: 0
      },
      rules: {
        brandName: [
          { required: true, message: '品牌名称不能为空', trigger: 'blur' }
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
      getQuoteBrandPage({ ...this.queryParams }).then((response) => {
        this.brandList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleAdd() {
      this.form = { id: undefined, brandName: '', imageUrl: '', sortOrder: 0 }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.brandForm && this.$refs.brandForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.form = {
        id: row.id,
        brandName: row.brandName,
        imageUrl: row.imageUrl,
        sortOrder: row.sortOrder
      }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm(`确认删除品牌“${row.brandName}”吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delQuoteBrand(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {
      })
    },
    handleSubmit() {
      this.$refs.brandForm.validate((valid) => {
        if (!valid) {
          return
        }
        saveQuoteBrand(this.form).then(() => {
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
.quote-brand-page {
  .quote-brand-card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-brand-empty-img {
    color: #999999;
    font-size: 12px;
  }

  .quote-danger-text {
    color: #f56c6c;
  }
}
</style>
