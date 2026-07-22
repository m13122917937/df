<!-- 订单搜索组件 -->
<template>
  <div class="order-search">
    <div class="search-form">
      <div class="search-item">
        <label class="search-label">业务类型</label>
        <el-select
          v-model="searchForm.orderType"
          placeholder="请选择"
          clearable
          size="small"
        >
          <el-option label="入仓" :value="1" />
          <el-option label="代发" :value="2" />
        </el-select>
      </div>
      <div class="search-item">
        <label class="search-label">订单号</label>
        <el-input
          v-model.trim="searchForm.orderCode"
          placeholder="请输入订单编号"
          clearable
          size="small"
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="search-item">
        <label class="search-label">商品名</label>
        <el-input
          v-model.trim="searchForm.productName"
          placeholder="请输入商品名称"
          clearable
          size="small"
          @keyup.enter.native="handleSearch"
        />
      </div>
      <div class="search-item">
        <label class="search-label">SKU</label>
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
.order-search {
  background: transparent;

  .search-form {
    display: flex;
    align-items: center;
    gap: 0;
    flex-wrap: nowrap;

    .search-item {
      display: flex;
      align-items: center;
      flex-shrink: 0;

      .search-label {
        font-size: 13px;
        color: #6E6E73;
        white-space: nowrap;
        flex-shrink: 0;
        margin-right: 8px;

        &::after {
          content: '：';
        }
      }

      .el-input {
        width: 200px;
        margin-right: 12px;
      }

      .el-select {
        width: 120px;
        margin-right: 12px;
      }
    }

    .search-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
      margin-left: 4px;
    }
  }
}
</style>
