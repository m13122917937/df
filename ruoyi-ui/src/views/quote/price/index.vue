<template>
  <div class="app-container quote-price-page">
    <el-card shadow="never" class="quote-price-filter-card">
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
        <el-form-item label="关键字">
          <el-input v-model="queryParams.productNameLike" clearable placeholder="商品名称/规格" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="quote-price-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-finished" :loading="saving" @click="handleSaveAll">批量保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="quote-price-table-card">
      <div slot="header" class="quote-price-table-header">
        <span>每日报价维护（零售、分销1、分销2 三档价格）</span>
        <span class="quote-price-tip">保存后批发报价页立即生效</span>
      </div>

      <el-table
        ref="quotePriceTable"
        v-loading="loading"
        :data="productList"
        border
        stripe
        size="medium"
      >
        <el-table-column prop="brand" label="品牌" min-width="110" show-overflow-tooltip />
        <el-table-column prop="category" label="品类" min-width="110" show-overflow-tooltip />
        <el-table-column prop="productName" label="商品名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="specName" label="规格/型号" min-width="150" show-overflow-tooltip />
        <el-table-column label="零售价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row.retailPrice"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="分销1价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row.distributor1Price"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="分销2价" min-width="140" align="center">
          <template slot-scope="scope">
            <el-input-number
              v-model="scope.row.distributor2Price"
              :min="0"
              :precision="2"
              :step="1"
              size="small"
              controls-position="right"
              style="width: 120px"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-check" :loading="saving" @click="handleSaveRow(scope.row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :page-sizes="[50, 100, 200]"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import {
  getQuoteProductPage,
  getQuoteBrandOptions,
  saveQuotePrices
} from '@/api/quote'

export default {
  name: 'QuotePriceUpdate',
  data() {
    return {
      loading: false,
      saving: false,
      total: 0,
      productList: [],
      brandOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 50,
        brandId: undefined,
        productNameLike: ''
      }
    }
  },
  created() {
    this.loadBrandOptions()
    this.getList()
  },
  methods: {
    loadBrandOptions() {
      getQuoteBrandOptions().then((response) => {
        this.brandOptions = response.data || []
      })
    },
    getList() {
      this.loading = true
      getQuoteProductPage({
        brandId: this.queryParams.brandId,
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
        pageSize: 50,
        brandId: undefined,
        productNameLike: ''
      }
      this.handleQuery()
    },
    handleSaveRow(row) {
      if (!this.hasAnyPrice(row)) {
        this.$message.warning('至少需要填写一个价格')
        return
      }
      this.saving = true
      saveQuotePrices({
        id: row.id,
        retailPrice: row.retailPrice,
        distributor1Price: row.distributor1Price,
        distributor2Price: row.distributor2Price
      }).then(() => {
        this.$message.success(`商品“${row.productName}”价格已保存`)
        this.saving = false
      }).catch(() => {
        this.saving = false
      })
    },
    handleSaveAll() {
      const rows = this.productList
      const invalid = rows.filter((row) => !this.hasAnyPrice(row))
      if (invalid.length > 0) {
        this.$message.warning(`请先为商品“${invalid[0].productName}”填写至少一个价格`)
        return
      }
      this.saving = true
      let success = 0
      let failed = 0
      const errors = []
      const doSave = (index) => {
        if (index >= rows.length) {
          this.saving = false
          let message = `保存完成：成功 ${success} 条，失败 ${failed} 条`
          if (errors.length > 0) {
            message += '；' + errors.slice(0, 5).join('；')
          }
          this.$message({ type: failed > 0 ? 'warning' : 'success', message, duration: 6000 })
          return
        }
        const row = rows[index]
        saveQuotePrices({
          id: row.id,
          retailPrice: row.retailPrice,
          distributor1Price: row.distributor1Price,
          distributor2Price: row.distributor2Price
        }).then(() => {
          success++
          doSave(index + 1)
        }).catch((error) => {
          failed++
          errors.push(`商品“${row.productName}”：${(error && error.message) || '保存失败'}`)
          doSave(index + 1)
        })
      }
      doSave(0)
    },
    hasAnyPrice(row) {
      return row.retailPrice !== null && row.retailPrice !== undefined && row.retailPrice !== ''
        || row.distributor1Price !== null && row.distributor1Price !== undefined && row.distributor1Price !== ''
        || row.distributor2Price !== null && row.distributor2Price !== undefined && row.distributor2Price !== ''
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-price-page {
  .quote-price-filter-card {
    margin-bottom: 16px;
  }

  .quote-price-table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .quote-price-tip {
    color: #909399;
    font-size: 12px;
  }
}
</style>
