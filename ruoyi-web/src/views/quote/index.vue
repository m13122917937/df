<template>
  <div class="quote-page">
    <div class="page-layout">
      <!-- 左侧品类筛选 -->
      <div class="filter-sidebar">
        <div class="filter-title">
          <i class="el-icon-collection-tag" />
          <span>品类</span>
        </div>
        <div class="category-list">
          <div
            :class="['category-item', { 'is-active': selectedCategory === '' }]"
            @click="selectCategory('')"
          >
            <span class="category-name">全部品类</span>
          </div>
          <div
            v-for="category in categories"
            :key="category"
            :class="['category-item', { 'is-active': selectedCategory === category }]"
            @click="selectCategory(category)"
          >
            <span class="category-name">{{ category }}</span>
          </div>
          <div v-if="categories.length === 0" class="category-empty">暂无品类</div>
        </div>
      </div>

      <!-- 右侧主内容 -->
      <div class="quote-content">
        <div class="content-filters">
          <div class="filter-group">
            <span class="filter-label">品牌：</span>
            <div class="filter-options">
              <span
                :class="['filter-option', { active: selectedBrand === '' }]"
                @click="selectBrand('')"
              >全部</span>
              <span
                v-for="brand in brands"
                :key="brand"
                :class="['filter-option', { active: selectedBrand === brand }]"
                @click="selectBrand(brand)"
              >{{ brand }}</span>
              <span v-if="brands.length === 0" class="filter-empty">暂无品牌</span>
            </div>
          </div>
          <div class="filter-group search-group">
            <span class="filter-label">搜索：</span>
            <div class="search-container">
              <el-input
                v-model.trim="searchKeyword"
                placeholder="请输入商品名称"
                prefix-icon="el-icon-search"
                clearable
                size="small"
                class="search-input"
                @keyup.enter.native="handleSearch"
                @clear="handleSearch"
              />
              <el-button size="mini" type="primary" @click="handleSearch">搜索</el-button>
            </div>
          </div>
        </div>

        <div class="table-card">
          <el-table
            v-loading="loading"
            :data="productList"
            stripe
            size="medium"
            style="width: 100%"
          >
            <template slot="empty">
              <div class="table-empty">暂无报价商品数据</div>
            </template>
            <el-table-column label="商品名称" prop="productName" min-width="200" show-overflow-tooltip>
              <template slot-scope="scope">
                <div class="product-name-cell">
                  <span class="product-name">{{ scope.row.productName || '-' }}</span>
                  <span v-if="scope.row.brand" class="product-brand">{{ scope.row.brand }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="规格/型号" prop="specName" min-width="160" show-overflow-tooltip>
              <template slot-scope="scope">{{ scope.row.specName || '-' }}</template>
            </el-table-column>
            <el-table-column
              v-for="tier in tiers"
              :key="'price-' + tier.id"
              :label="tier.tierName"
              min-width="120"
              align="right"
            >
              <template slot-scope="scope">
                <span class="price-text">¥{{ formatPrice(getPriceByTier(scope.row, tier.id)) }}</span>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper">
            <el-pagination
              :current-page="pagination.current"
              :page-sizes="[20, 50, 100]"
              :page-size="pagination.size"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              background
              class="custom-pagination"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  apiGetQuoteTierList,
  apiGetQuoteProductList,
  apiGetQuoteBrandList,
  apiGetQuoteCategoryList
} from '@/api/quote'

export default {
  name: 'QuotePrice',
  data() {
    return {
      loading: false,
      tiers: [],
      brands: [],
      categories: [],
      selectedBrand: '',
      selectedCategory: '',
      searchKeyword: '',
      productList: [],
      pagination: {
        current: 1,
        size: 20,
        total: 0
      }
    }
  },
  created() {
    this.fetchMeta()
    this.fetchProductList()
  },
  methods: {
    fetchMeta() {
      apiGetQuoteTierList().then((res) => {
        this.tiers = (res && res.data) || []
      })
      this.fetchBrands()
      this.fetchCategories()
    },
    fetchBrands() {
      apiGetQuoteBrandList().then((res) => {
        this.brands = (res && res.data) || []
      })
    },
    fetchCategories() {
      apiGetQuoteCategoryList().then((res) => {
        this.categories = (res && res.data) || []
      })
    },
    fetchProductList() {
      this.loading = true
      apiGetQuoteProductList({
        brand: this.selectedBrand,
        category: this.selectedCategory,
        productNameLike: this.searchKeyword
      }, {
        pageNum: this.pagination.current,
        pageSize: this.pagination.size
      }).then((res) => {
        if (res && res.code === 200) {
          this.productList = res.rows || []
          this.pagination.total = res.total || 0
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    selectBrand(brand) {
      this.selectedBrand = brand
      this.pagination.current = 1
      this.fetchProductList()
    },
    selectCategory(category) {
      this.selectedCategory = category
      this.pagination.current = 1
      this.fetchProductList()
    },
    handleSearch() {
      this.pagination.current = 1
      this.fetchProductList()
    },
    handleSizeChange(size) {
      this.pagination.size = size
      this.fetchProductList()
    },
    handleCurrentChange(current) {
      this.pagination.current = current
      this.fetchProductList()
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
    }
  }
}
</script>

<style lang="scss" scoped>
.quote-page {
  height: 100%;
  background: var(--bg-page, #f2f2f7);
  padding: 16px;
  box-sizing: border-box;

  .page-layout {
    display: flex;
    gap: 16px;
    height: 100%;
  }

  .filter-sidebar {
    width: 220px;
    flex-shrink: 0;
    background: #ffffff;
    border-radius: 12px;
    padding: 16px;
    box-sizing: border-box;
    overflow-y: auto;

    .filter-title {
      display: flex;
      align-items: center;
      gap: 6px;
      font-weight: 600;
      font-size: 14px;
      color: var(--text-primary, #1d1d1f);
      padding-bottom: 12px;
      border-bottom: 1px solid #f0f0f0;
      margin-bottom: 12px;
    }

    .category-item {
      padding: 9px 12px;
      border-radius: 8px;
      cursor: pointer;
      color: #555555;
      font-size: 13px;
      transition: all 0.2s ease;

      &:hover {
        background: #f5f7fa;
      }

      &.is-active {
        background: rgba(37, 99, 255, 0.08);
        color: var(--color-primary, #2563ff);
        font-weight: 600;
      }
    }

    .category-empty {
      padding: 12px;
      color: #999999;
      font-size: 13px;
      text-align: center;
    }
  }

  .quote-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .content-filters {
    background: #ffffff;
    border-radius: 12px;
    padding: 14px 16px;
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: center;

    .filter-group {
      display: flex;
      align-items: center;
      gap: 8px;

      .filter-label {
        color: #555555;
        font-size: 13px;
        flex-shrink: 0;
      }

      .filter-options {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;

        .filter-option {
          padding: 5px 12px;
          border-radius: 16px;
          background: #f5f7fa;
          color: #555555;
          font-size: 13px;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: #eef1f6;
          }

          &.active {
            background: var(--color-primary, #2563ff);
            color: #ffffff;
          }
        }

        .filter-empty {
          color: #999999;
          font-size: 13px;
        }
      }

      .search-container {
        display: flex;
        align-items: center;
        gap: 8px;

        .search-input {
          width: 260px;
        }
      }
    }
  }

  .table-card {
    flex: 1;
    min-height: 0;
    background: #ffffff;
    border-radius: 12px;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .table-empty {
      padding: 40px 0;
      color: #999999;
      text-align: center;
    }

    .product-name-cell {
      display: flex;
      flex-direction: column;
      gap: 2px;

      .product-name {
        color: var(--text-primary, #1d1d1f);
        font-weight: 500;
      }

      .product-brand {
        color: #999999;
        font-size: 12px;
      }
    }

    .price-text {
      color: #d4380d;
      font-weight: 600;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      padding-top: 14px;
    }
  }
}
</style>
