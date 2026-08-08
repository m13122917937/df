<template>
  <div class="app-container quote-product-page">
    <el-card shadow="never" class="quote-product-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="品牌">
          <el-select v-model="queryParams.brandId" clearable filterable placeholder="请选择品牌" style="width: 180px" @change="handleQuery">
            <el-option
              v-for="brand in brandOptions"
              :key="brand.id"
              :label="brand.brandName"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品类">
          <el-select v-model="queryParams.categoryId" clearable filterable placeholder="请选择品类" style="width: 180px" @change="handleQuery">
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="queryParams.productNameLike" clearable placeholder="请输入商品名称" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="quote-product-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="quote-product-table-card">
      <div slot="header" class="quote-product-table-header">
        <span>商品列表</span>
        <div>
          <el-upload
            :show-file-list="false"
            :action="importUrl"
            :headers="uploadHeaders"
            accept=".xlsx,.xls"
            :on-success="handleImportSuccess"
            :on-error="handleImportError"
            style="display: inline-block; margin-right: 8px"
          >
            <el-button size="small" icon="el-icon-upload2">导入</el-button>
          </el-upload>
          <el-button size="small" icon="el-icon-download" @click="handleExport">导出</el-button>
          <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增商品</el-button>
        </div>
      </div>

      <el-table
        ref="quoteProductTable"
        v-loading="loading"
        :data="productList"
        border
        stripe
        size="medium"
      >
        <el-table-column prop="brand" label="品牌" min-width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="品类" min-width="120" show-overflow-tooltip />
        <el-table-column prop="productName" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="specName" label="规格/型号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="productCode" label="商品编码" min-width="130" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
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

    <el-dialog
      :title="form.id ? '编辑商品' : '新增商品'"
      :visible.sync="dialogVisible"
      width="640px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form ref="productForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="form.brandId" filterable placeholder="请选择品牌" style="width: 100%">
            <el-option
              v-for="brand in brandOptions"
              :key="brand.id"
              :label="brand.brandName"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品类" prop="categoryId">
          <el-select v-model="form.categoryId" filterable placeholder="请选择品类" style="width: 100%">
            <el-option
              v-for="category in categoryOptions"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="规格/型号">
          <el-input v-model="form.specName" placeholder="请输入规格/型号" maxlength="255" />
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="form.productCode" placeholder="请输入商品编码" maxlength="128" />
        </el-form-item>
        <el-form-item label="排序">
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
import { getToken } from '@/utils/auth'
import {
  getQuoteProductPage,
  saveQuoteProduct,
  delQuoteProduct,
  getQuoteBrandOptions,
  getQuoteCategoryOptions,
  exportQuoteProduct
} from '@/api/quote'

export default {
  name: 'QuoteProduct',
  data() {
    return {
      importUrl: process.env.VUE_APP_BASE_API + '/quote/product/import',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      loading: false,
      total: 0,
      productList: [],
      brandOptions: [],
      categoryOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        brandId: undefined,
        categoryId: undefined,
        productNameLike: ''
      },
      dialogVisible: false,
      form: {
        id: undefined,
        brandId: undefined,
        categoryId: undefined,
        brand: '',
        category: '',
        productName: '',
        specName: '',
        productCode: '',
        sortOrder: 0
      },
      rules: {
        brandId: [
          { required: true, message: '请选择品牌', trigger: 'change' }
        ],
        categoryId: [
          { required: true, message: '请选择品类', trigger: 'change' }
        ],
        productName: [
          { required: true, message: '商品名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadBrandOptions()
    this.loadCategoryOptions()
    this.getList()
  },
  methods: {
    loadBrandOptions() {
      getQuoteBrandOptions().then((response) => {
        this.brandOptions = response.data || []
      })
    },
    loadCategoryOptions() {
      getQuoteCategoryOptions().then((response) => {
        this.categoryOptions = response.data || []
      })
    },
    getList() {
      this.loading = true
      getQuoteProductPage({
        brandId: this.queryParams.brandId,
        categoryId: this.queryParams.categoryId,
        productNameLike: this.queryParams.productNameLike,
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }).then((response) => {
        this.productList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        brandId: undefined,
        categoryId: undefined,
        productNameLike: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.resetForm()
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.productForm && this.$refs.productForm.clearValidate()
      })
    },
    handleEdit(row) {
      this.resetForm()
      this.form.id = row.id
      this.form.brandId = row.brandId
      this.form.categoryId = row.categoryId
      this.form.productName = row.productName
      this.form.specName = row.specName
      this.form.productCode = row.productCode
      this.form.sortOrder = row.sortOrder
      this.dialogVisible = true
    },
    resetForm() {
      this.form = {
        id: undefined,
        brandId: undefined,
        categoryId: undefined,
        brand: '',
        category: '',
        productName: '',
        specName: '',
        productCode: '',
        sortOrder: 0
      }
    },
    handleSubmit() {
      this.$refs.productForm.validate((valid) => {
        if (!valid) {
          return
        }
        const payload = {
          id: this.form.id,
          brandId: this.form.brandId,
          categoryId: this.form.categoryId,
          productName: this.form.productName,
          specName: this.form.specName,
          productCode: this.form.productCode,
          sortOrder: this.form.sortOrder
        }
        saveQuoteProduct(payload).then(() => {
          this.$message.success('保存成功')
          this.dialogVisible = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$confirm(`确认删除商品“${row.productName}”吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delQuoteProduct(row.id)
      }).then(() => {
        this.$message.success('删除成功')
        this.getList()
      }).catch(() => {
      })
    },
    handleExport() {
      exportQuoteProduct({
        brandId: this.queryParams.brandId,
        categoryId: this.queryParams.categoryId,
        productNameLike: this.queryParams.productNameLike
      })
    },
    handleImportSuccess(response) {
      if (response && response.code === 200) {
        const data = response.data || {}
        let message = `导入完成：成功 ${data.success || 0} 条，失败 ${data.failed || 0} 条`
        const errors = data.errors || []
        if (errors.length > 0) {
          message += '；' + errors.slice(0, 5).join('；')
        }
        this.$message({ type: data.failed > 0 ? 'warning' : 'success', message, duration: 8000 })
        this.getList()
      } else {
        this.$message.error((response && response.msg) || '导入失败')
      }
    },
    handleImportError() {
      this.$message.error('导入失败，请检查文件格式')
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-product-page {
  .quote-product-filter-card {
    margin-bottom: 16px;
  }

  .quote-product-table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-danger-text {
    color: #f56c6c;
  }
}
</style>
