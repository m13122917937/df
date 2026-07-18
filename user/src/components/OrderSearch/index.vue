<!-- 订单搜索组件 -->
<template>
  <div class="order-search">
    <div class="search-form">
      <div class="search-item">
        <label class="search-label">业务类型：</label>
        <el-select
          v-model="searchForm.orderType"
          placeholder="请选择业务类型"
          clearable
          size="small"
        >
          <el-option label="入仓" :value="1" />
          <el-option label="代发" :value="2" />
        </el-select>
      </div>
      <div class="search-item">
        <label class="search-label">订单号：</label>
        <el-input
          v-model.trim="searchForm.orderCode"
          placeholder="请输入订单编号"
          clearable
          size="small"
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="search-item">
        <label class="search-label">商品名：</label>
        <el-input
          v-model.trim="searchForm.productName"
          placeholder="请输入商品名称"
          clearable
          size="small"
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="search-item">
        <label class="search-label">SKU：</label>
        <el-input
          v-model.trim="searchForm.skuName"
          placeholder="请输入SKU名称"
          clearable
          size="small"
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="search-actions">
        <el-button
          type="primary"
          icon="el-icon-search"
          size="small"
          @click="handleSearch"
        >
          搜索
        </el-button>
        <el-button
          icon="el-icon-refresh"
          size="small"
          @click="handleReset"
        >
          重置
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { throttle } from '@/utils'

export default {
  name: 'OrderSearch',
  props: {
    // 初始搜索值
    value: {
      type: Object,
      default: () => ({
        orderCode: '',
        productName: '',
        skuName: '',
        orderType: ''
      })
    },
    // 是否显示所有搜索字段
    showAllFields: {
      type: Boolean,
      default: true
    },
    // 自定义搜索字段
    customFields: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      searchForm: {
        orderCode: '',
        productName: '',
        skuName: '',
        orderType: ''
      }
    }
  },
  watch: {
    value: {
      handler(newVal) {
        this.searchForm = { ...newVal }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    // 搜索处理
    handleSearch: throttle(function() {
      this.$emit('search', this.searchForm)
    }, 500),
    // 清空搜索
    handleSearchClear() {
      this.searchForm = {
        orderCode: '',
        productName: '',
        skuName: '',
        orderType: ''
      }
      this.$emit('search', this.searchForm)
    },
    // 重置搜索
    handleReset() {
      this.searchForm = {
        orderCode: '',
        productName: '',
        skuName: '',
        orderType: ''
      }
      this.$emit('reset', this.searchForm)
    },
    // 获取当前搜索表单数据
    getSearchData() {
      return { ...this.searchForm }
    },
    // 设置搜索表单数据
    setSearchData(data) {
      this.searchForm = { ...data }
    }
  }
}
</script>

<style lang="scss" scoped>
// 订单搜索样式
.order-search {
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;

  .search-form {
    display: flex;
    gap: 16px;
    align-items: flex-end;
    flex-wrap: wrap;

    .search-item {
      display: flex;
      align-items: center;

      .search-label {
        font-size: 14px;
        font-weight: 500;
        color: #606266;
        width: 70px;
        text-align: right;
        flex-shrink: 0;
      }

      .el-input {
        width: 220px;
      }

      .el-select {
        width: 130px;
      }
    }

    .search-actions {
      display: flex;
      gap: 8px;
      align-items: center;

      .el-button {
        border-radius: 6px;
        font-weight: 500;
        transition: all 0.3s ease;

        &.el-button--primary {
          background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
          border: none;
          box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba(64, 158, 255, 0.4);
          }
        }

        &:not(.el-button--primary) {
          border: 1px solid #dcdfe6;
          color: #606266;

          &:hover {
            color: #409EFF;
            border-color: #409EFF;
            background: rgba(64, 158, 255, 0.05);
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .order-search .search-form {
    flex-direction: column;
    align-items: stretch;

    .search-item {
      min-width: auto;
    }

    .search-actions {
      margin-top: 12px;
      justify-content: center;
    }
  }
}

@media (max-width: 768px) {
  .order-search {
    padding: 12px;

    .search-form {
      gap: 12px;

      .search-actions {
        flex-direction: column;
        gap: 8px;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
