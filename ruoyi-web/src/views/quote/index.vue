<template>
  <div class="quote-page">
    <div class="quote-content">
      <!-- 头部筛选：品牌 + 搜索（与落地零售风格一致） -->
      <div class="content-filters">
        <div class="filter-group">
          <span class="filter-label">品牌：</span>
          <div class="filter-options">
            <span
              :class="['filter-option', { active: selectedBrandId === null }]"
              @click="selectBrand(null)"
            >全部品牌</span>
            <span
              v-for="brand in brands"
              :key="brand.id"
              :class="['filter-option', { active: selectedBrandId === brand.id }]"
              @click="selectBrand(brand)"
            >{{ brand.brandName }}</span>
            <span v-if="brands.length === 0" class="filter-empty">暂无品牌</span>
          </div>
        </div>
        <div class="filter-group search-group">
          <span class="filter-label">搜索：</span>
          <div class="search-container">
            <el-input
              v-model.trim="searchKeyword"
              placeholder="请输入商品名称模糊搜索"
              prefix-icon="el-icon-search"
              clearable
              size="small"
              class="sku-search-input"
              @keyup.enter.native="handleSearch"
              @clear="handleSearch"
            />
            <el-button size="small" type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </div>

      <!-- 商品报价列表：一行 2 条，左右结构 -->
      <div class="table-card">
        <div v-loading="loading" class="product-list">
          <div
            v-for="product in productList"
            :key="product.id"
            class="product-item"
            @click="openHistory(product)"
          >
            <div class="item-info">
              <span class="item-name">{{ product.productName || '-' }}</span>
              <span class="item-spec">{{ product.specName || '-' }}</span>
            </div>
            <div class="item-prices">
              <span class="item-price">¥{{ formatPrice(product.currentPrice) }}</span>
            </div>
          </div>
          <div v-if="!loading && productList.length === 0" class="list-empty">暂无报价商品数据</div>
        </div>
      </div>
    </div>

    <!-- 历史报价弹窗 -->
    <el-dialog
      :title="historyProduct ? `${historyProduct.productName} ${historyProduct.specName || '-'} - 历史报价` : '历史报价'"
      :visible.sync="historyDialogVisible"
      width="680px"
      append-to-body
    >
      <el-table :data="historyList" border stripe size="medium" max-height="420">
        <el-table-column label="报价时间" min-width="170" align="center">
          <template slot-scope="scope">{{ formatDateTime(scope.row.updateTime || scope.row.quoteDate) }}</template>
        </el-table-column>
        <el-table-column label="价格" min-width="130" align="right">
          <template slot-scope="scope">¥{{ formatPrice(scope.row.price) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="!loadingHistory && historyList.length === 0" class="history-empty">暂无历史报价</div>
    </el-dialog>
  </div>
</template>

<script>
import {
  apiGetQuoteProductList,
  apiGetQuoteBrandList,
  apiGetQuoteHistory
} from '@/api/quote'

export default {
  name: 'QuotePrice',
  data() {
    return {
      loading: false,
      brands: [],
      selectedBrandId: null,
      searchKeyword: '',
      productList: [],
      historyDialogVisible: false,
      loadingHistory: false,
      historyList: [],
      historyProduct: null
    }
  },
  created() {
    this.fetchBrands()
    this.fetchProductList()
  },
  methods: {
    fetchBrands() {
      apiGetQuoteBrandList().then((res) => {
        this.brands = (res && res.data) || []
      })
    },
    fetchProductList() {
      this.loading = true
      apiGetQuoteProductList({
        brandId: this.selectedBrandId,
        productNameLike: this.searchKeyword
      }, {
        pageNum: 1,
        pageSize: 10000
      }).then((res) => {
        if (res && res.code === 200) {
          this.productList = (res.rows || []).filter(
            (row) => row.currentPrice !== null && row.currentPrice !== undefined
          )
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    selectBrand(brand) {
      this.selectedBrandId = brand ? brand.id : null
      this.fetchProductList()
    },
    handleSearch() {
      this.fetchProductList()
    },
    openHistory(product) {
      this.historyProduct = product
      this.historyList = []
      this.historyDialogVisible = true
      this.loadingHistory = true
      apiGetQuoteHistory(product.id).then((res) => {
        this.historyList = (res && res.data) || []
        this.loadingHistory = false
      }).catch(() => {
        this.loadingHistory = false
      })
    },
    formatPrice(price) {
      if (price === null || price === undefined || price === '') {
        return '-'
      }
      return Number(price).toFixed(2)
    },
    formatDateTime(dateTime) {
      if (!dateTime) {
        return '-'
      }
      return String(dateTime).replace('T', ' ').slice(0, 19)
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

  .quote-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .content-filters {
    background: linear-gradient(#ffffff, #ffffff) padding-box,
                linear-gradient(135deg, #e9f3ff, #f3e9ff) border-box;
    padding: 16px 20px;
    border-radius: 6px;
    box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
    border: 1px solid transparent;
    position: sticky;
    top: 8px;
    z-index: 3;
    backdrop-filter: saturate(1.1);

    .filter-group {
      display: flex;
      align-items: center;

      .filter-label {
        font-weight: 500;
        color: #333;
        min-width: 60px;
      }

      .filter-options {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;

        .filter-option {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 8px 16px;
          border: 1px solid #e4e7ed;
          border-radius: 20px;
          cursor: pointer;
          font-size: 14px;
          transition: all 0.2s ease;
          background: linear-gradient(180deg, #ffffff, #fafbfc);
          white-space: nowrap;

          &:hover {
            border-color: #1677FF;
            color: #1677FF;
            box-shadow: 0 6px 16px rgba(64, 158, 255, 0.12);
            transform: translateY(-1px);
          }

          &.active {
            background: linear-gradient(135deg, #3395FF, #1677FF);
            color: #fff;
            border-color: transparent;
            box-shadow: 0 12px 28px rgba(64, 158, 255, 0.24);
          }
        }

        .filter-empty {
          color: #999999;
          font-size: 13px;
        }
      }
    }

    .search-group {
      margin-top: 16px;
      align-items: center;

      .search-container {
        flex: 1;
        max-width: 460px;
        display: flex;
        align-items: center;
        gap: 10px;

        .sku-search-input {
          flex: 1;

          .el-input__inner {
            border-radius: 6px;
            border: 1px solid #ddd;
            transition: all 0.3s;

            &:focus {
              border-color: #1677FF;
              box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.12);
            }
          }
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

    .product-list {
      height: 100%;
      overflow-y: auto;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
      align-content: start;

      .product-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
        border: 1px solid #eceff3;
        border-radius: 8px;
        padding: 10px 12px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          border-color: rgba(37, 99, 255, 0.4);
          background: #f7f9fc;
        }

        .item-info {
          display: flex;
          align-items: center;
          gap: 8px;
          min-width: 0;

          .item-name {
            color: var(--text-primary, #1d1d1f);
            font-weight: 600;
            font-size: 14px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            flex-shrink: 0;
          }

          .item-spec {
            color: #999999;
            font-size: 12px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .item-prices {
          display: flex;
          align-items: center;
          flex-shrink: 0;

          .item-price {
            color: #d4380d;
            font-weight: 600;
            font-size: 15px;
          }
        }
      }

      .list-empty {
        grid-column: 1 / -1;
        padding: 60px 0;
        color: #999999;
        text-align: center;
      }
    }
  }

  .history-empty {
    padding: 24px 0;
    color: #999999;
    text-align: center;
  }
}
</style>
