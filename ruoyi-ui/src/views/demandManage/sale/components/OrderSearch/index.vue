<!-- 订单搜索组件 -->
<template>
  <div class="order-search">
    <div class="search-form">
      <div class="search-item">
        <label class="search-label">供应商：</label>
        <el-select
          v-model="searchForm.companyId"
          placeholder="请输入供应商名称"
          clearable
          filterable
          remote
          reserve-keyword
          :remote-method="handleSupplierRemote"
          :loading="supplierLoading"
          @visible-change="handleSupplierVisible"
          @change="handleSupplierChange"
          @clear="handleSupplierClear"
          class="search-select"
        >
          <el-option v-for="s in supplierOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </div>
      <div class="search-item">
        <label class="search-label">商品名：</label>
        <el-input
          v-model.trim="searchForm.productName"
          placeholder="请输入商品名称"
          clearable
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
    <!-- 工具栏插槽（参考 wholesale），父组件可通过 slot name="toolbar" 注入额外控件 -->
    <div v-if="$slots.toolbar" class="toolbar-slot">
      <slot name="toolbar"></slot>
    </div>
  </div>
</template>

<script>
import { throttle } from '@/utils'
import { getBusinessCompanyListApi } from '@/api/business'

export default {
  name: 'OrderSearch',
  props: {
    // 初始搜索值
    value: {
      type: Object,
      default: () => ({
        productName: '',
        companyId: ''
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
        productName: '',
        companyId: ''
      },
      supplierOptions: [],
      supplierLoading: false
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
        productName: ''
      }
      this.$emit('search', this.searchForm)
    },
    // 重置搜索
    handleReset() {
      this.searchForm = {
        productName: '',
        companyId: ''
      }
      this.supplierOptions = [];
      this.supplierLoading = false;
      this.$emit('reset', this.searchForm)
    },
    // 供应商远程搜索
    async handleSupplierRemote(keyword = '') {
      const trimmed = (keyword || '').trim()
      this.supplierLoading = true
      try {
        const pageData = {
          pageNum: 1,
          pageSize: 30
        }
        const params = {}
        if (trimmed) {
          params.companyNameLike = trimmed
        }
        const res = await getBusinessCompanyListApi(params,pageData)
        const list = res?.rows || res?.data || []
        this.supplierOptions = Array.isArray(list)
          ? list.map(item => ({
              label: item.companyName,
              value: item.id,
              raw: item
            }))
          : []
      } catch (error) {
        console.error('获取供应商列表失败', error)
        this.supplierOptions = []
      } finally {
        this.supplierLoading = false
      }
    },
    handleSupplierVisible(visible) {
      if (visible && !this.supplierOptions.length) {
        this.handleSupplierRemote()
      }
    },
    handleSupplierChange(val) {
      // 主动触发搜索以便页面同步
      this.$emit('search', this.searchForm)
    },
    handleSupplierClear() {
      this.searchForm.companyId = ''
      this.$emit('search', this.searchForm)
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
// 订单搜索样式（参考 wholesale/searchSection 视觉风格）
.order-search {
  margin-bottom: 10px;
  padding: 10px;
  background: linear-gradient(135deg, var(--bg-hover) 0%, var(--bg-card) 100%);
  border-radius: 12px;
  border: 1px solid var(--border-tags);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c);
  }

  .search-form {
    display: flex;
    gap: 12px;
    align-items: flex-end;
    flex-wrap: wrap;

    .search-item {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 8px;

      .search-label {
        font-size: 14px;
        font-weight: 500;
        color: #606266;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 6px;
        white-space: nowrap;
        min-width: fit-content;
        flex-shrink: 0;
      }

      .search-label::before {
        content: "";
        width: 4px;
        height: 4px;
        background: #409eff;
        border-radius: 50%;
      }

      .el-input,
      .el-select,
      .el-input-number {
        width: 100%;
      }
    }

    .search-actions {
      display: flex;
      width: 200px;
      justify-content: space-between;
      align-items: center;

      .el-button {
        height: 36px;
        font-size: 14px;
        border-radius: 8px;
      }

      .el-button--primary {
          background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
          border: none;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }
    }
  }

  .search-select {
    width: 100%;
  }

  .toolbar-slot {
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid var(--border-tags);
    display: flex;
    justify-content: flex-end;
    align-items: center;
    min-height: 40px;
    gap: 12px;
  }
}

// 响应式设计（保持原有布局行为）
@media (max-width: 1200px) {
  .order-search .search-form {
    flex-direction: column;
    align-items: stretch;

    .search-item {
      width: 100%;
      flex-direction: column;
      align-items: flex-start;
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
