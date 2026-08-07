<template>
  <div class="app-container quote-product-page">
    <el-card shadow="never" class="quote-product-filter-card">
      <el-form :inline="true" :model="queryParams" size="small" @submit.native.prevent>
        <el-form-item label="品牌">
          <el-input v-model="queryParams.brand" clearable placeholder="请输入品牌" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="品类">
          <el-input v-model="queryParams.category" clearable placeholder="请输入品类" @keyup.enter.native="handleQuery" />
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
        <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="handleAdd"
        >新增商品</el-button>
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
        <el-table-column
          v-for="tier in tierOptions"
          :key="'tier-' + tier.id"
          :label="tier.tierName"
          min-width="120"
          align="right"
        >
          <template slot-scope="scope">
            {{ formatPrice(getPriceByTier(scope.row, tier.id)) }}
          </template>
        </el-table-column>
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
        <el-form-item label="品牌" prop="brand">
          <el-input v-model="form.brand" placeholder="请输入品牌" maxlength="128" />
        </el-form-item>
        <el-form-item label="品类" prop="category">
          <el-input v-model="form.category" placeholder="请输入品类" maxlength="128" />
        </el-form-item>
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" maxlength="255" />
        </el-form-item>
        <el-form-item label="规格/型号">
          <el-input v-model="form.specName" placeholder="请输入规格/型号" maxlength="255" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-divider content-position="left">档位价格</el-divider>
        <el-form-item
          v-for="tier in tierOptions"
          :key="'price-' + tier.id"
          :label="tier.tierName"
          :prop="'priceMap.' + tier.id"
          :rules="[{ validator: validatePrice, trigger: 'blur' }]"
        >
          <el-input-number
            v-model="form.priceMap[tier.id]"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
            style="width: 200px"
          />
        </el-form-item>
        <el-alert
          v-if="tierOptions.length === 0"
          title="请先在“价格档位管理”中创建档位，再填写商品价格"
          type="warning"
          :closable="false"
          show-icon
        />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getQuoteProductPage, saveQuoteProduct, delQuoteProduct, getQuoteTierOptions } from '@/api/quote'

export default {
  name: 'QuoteProduct',
  data() {
    return {
      loading: false,
      total: 0,
      productList: [],
      tierOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        brand: '',
        category: '',
        productNameLike: ''
      },
      dialogVisible: false,
      form: {
        id: undefined,
        brand: '',
        category: '',
        productName: '',
        specName: '',
        sortOrder: 0,
        priceMap: {}
      },
      rules: {
        brand: [
          { required: true, message: '品牌不能为空', trigger: 'blur' }
        ],
        productName: [
          { required: true, message: '商品名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadTierOptions()
    this.getList()
  },
  methods: {
    loadTierOptions() {
      getQuoteTierOptions().then((response) => {
        this.tierOptions = response.data || []
      })
    },
    getList() {
      this.loading = true
      getQuoteProductPage({
        brand: this.queryParams.brand,
        category: this.queryParams.category,
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
        brand: '',
        category: '',
        productNameLike: ''
      }
      this.handleQuery()
    },
    getPriceByTier(row, tierId) {
      const price = (row.prices || []).find((item) => item.tierId === tierId)
      return price ? price.price : null
    },
    formatPrice(price) {
      if (price === null || price === undefined || price === '') {
        return '-'
      }
      return Number(price).toFixed(2)
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
      this.form.brand = row.brand
      this.form.category = row.category
      this.form.productName = row.productName
      this.form.specName = row.specName
      this.form.sortOrder = row.sortOrder
      ;(row.prices || []).forEach((item) => {
        this.$set(this.form.priceMap, item.tierId, item.price)
      })
      this.dialogVisible = true
    },
    resetForm() {
      this.form = {
        id: undefined,
        brand: '',
        category: '',
        productName: '',
        specName: '',
        sortOrder: 0,
        priceMap: {}
      }
    },
    validatePrice(rule, value, callback) {
      if (value === null || value === undefined || value === '') {
        callback(new Error('请输入该档位价格'))
        return
      }
      callback()
    },
    handleSubmit() {
      this.$refs.productForm.validate((valid) => {
        if (!valid) {
          return
        }
        const prices = this.tierOptions
          .map((tier) => ({
            tierId: tier.id,
            price: this.form.priceMap[tier.id]
          }))
          .filter((item) => item.price !== null && item.price !== undefined && item.price !== '')
        if (prices.length === 0) {
          this.$message.warning('至少需要填写一个档位价格')
          return
        }
        const payload = {
          id: this.form.id,
          brand: this.form.brand,
          category: this.form.category,
          productName: this.form.productName,
          specName: this.form.specName,
          sortOrder: this.form.sortOrder,
          prices
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
