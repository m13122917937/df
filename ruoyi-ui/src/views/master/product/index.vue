<template>
  <div class="app-container master-product-page">
    <section class="master-product-card master-product-filter-card">
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
        <el-form-item label="SKU编码">
          <el-input v-model="queryParams.skuCode" clearable placeholder="请输入SKU编码" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item class="master-product-filter-actions">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="master-product-card master-product-table-card">
      <div class="master-product-card-title">
        <div>
          <h3>商品列表</h3>
          <p>商品数据由吉客云同步维护，按 SKU 粒度供各业务模块统一查询。</p>
        </div>
      </div>
      <div class="master-product-table-wrap">
        <el-table ref="productTable" v-loading="loading" :data="filteredProductList" height="100%" border stripe>
          <el-table-column
            v-for="column in visibleColumns"
            :key="column.key"
            :prop="column.key"
            :label="column.label"
            :min-width="column.minWidth"
            :show-overflow-tooltip="column.showOverflowTooltip"
          >
            <template #header>
              <div class="master-product-column-header">
                <FilterHeader
                  :label="column.label"
                  :value="columnSearch[column.key]"
                  :options="colFilterOptions[column.key] || []"
                  @update:value="updateColumnFilter(column.key, $event)"
                />
              </div>
            </template>
            <template slot-scope="scope">
              {{ getColumnValue(scope.row, column.key) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>

    <section v-show="total > 0" class="master-product-card master-product-pagination-card">
      <pagination
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        :auto-scroll="false"
        @pagination="getList"
      />
    </section>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import { getMasterProductList } from '@/api/master'
import FilterHeader from '@/views/business/manage/components/FilterHeader.vue'

const PRODUCT_COLUMNS = [
  { key: 'brand', label: '品牌', minWidth: 120, showOverflowTooltip: true },
  { key: 'category', label: '品类', minWidth: 120, showOverflowTooltip: true },
  { key: 'skuCode', label: 'SKU编码', minWidth: 150, showOverflowTooltip: true },
  { key: 'productName', label: '商品名称', minWidth: 200, showOverflowTooltip: true },
  { key: 'specName', label: '规格', minWidth: 180, showOverflowTooltip: true },
  { key: 'snType', label: '串码管理', minWidth: 130, showOverflowTooltip: false },
  { key: 'createTime', label: '创建时间', minWidth: 180, showOverflowTooltip: false }
]

function createColumnSearch() {
  return PRODUCT_COLUMNS.reduce((search, column) => {
    search[column.key] = []
    return search
  }, {})
}

export default {
  name: 'MasterProduct',
  components: { FilterHeader },
  data() {
    return {
      loading: false,
      total: 0,
      productList: [],
      columnOrder: PRODUCT_COLUMNS.map(column => column.key),
      columnSearch: createColumnSearch(),
      queryParams: this.createQueryParams()
    }
  },
  computed: {
    visibleColumns() {
      return this.columnOrder.map(key => PRODUCT_COLUMNS.find(column => column.key === key))
    },
    colFilterOptions() {
      return PRODUCT_COLUMNS.reduce((options, column) => {
        const values = this.productList
          .map(row => this.getColumnValue(row, column.key))
          .filter(value => value !== '-')
        options[column.key] = [...new Set(values)].map(value => ({ text: value, value }))
        return options
      }, {})
    },
    filteredProductList() {
      return this.productList.filter(row => this.matchesColumnFilters(row))
    }
  },
  created() {
    this.getList()
  },
  mounted() {
    this.$nextTick(this.initColumnDrag)
  },
  beforeDestroy() {
    this.destroyColumnDrag()
  },
  methods: {
    createQueryParams() {
      return {
        pageNum: 1,
        pageSize: 20,
        brand: '',
        category: '',
        productNameLike: '',
        skuCode: ''
      }
    },
    getList() {
      this.loading = true
      getMasterProductList(this.queryParams).then(response => {
        this.productList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = this.createQueryParams()
      this.getList()
    },
    updateColumnFilter(key, values) {
      this.$set(this.columnSearch, key, values)
    },
    getColumnValue(row, key) {
      if (key === 'snType') {
        return this.formatSnType(row.snType)
      }
      return row[key] || '-'
    },
    formatSnType(value) {
      const labelMap = {
        0: '不启用',
        1: '序列号',
        2: '随机序列号'
      }
      return labelMap[value] || '-'
    },
    matchesColumnFilters(row) {
      return PRODUCT_COLUMNS.every(column => {
        const values = this.columnSearch[column.key]
        return !values.length || values.includes(this.getColumnValue(row, column.key))
      })
    },
    initColumnDrag() {
      const table = this.$refs.productTable
      const headerRow = table && table.$el.querySelector('.el-table__header-wrapper tr')
      if (!headerRow) {
        return
      }
      this.destroyColumnDrag()
      this.columnSortable = Sortable.create(headerRow, {
        animation: 150,
        fallbackTolerance: 6,
        onEnd: ({ oldIndex, newIndex }) => this.moveColumn(oldIndex, newIndex)
      })
    },
    destroyColumnDrag() {
      if (this.columnSortable) {
        this.columnSortable.destroy()
        this.columnSortable = null
      }
    },
    moveColumn(oldIndex, newIndex) {
      if (oldIndex === newIndex || oldIndex === undefined || newIndex === undefined) {
        return
      }
      const movedKey = this.columnOrder.splice(oldIndex, 1)[0]
      this.columnOrder.splice(newIndex, 0, movedKey)
      this.destroyColumnDrag()
      this.$nextTick(() => {
        this.$refs.productTable.doLayout()
        this.initColumnDrag()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.master-product-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 112px);
  min-height: 0;
  overflow: hidden;
  box-sizing: border-box;
  color: var(--nl-color);
}

.master-product-card {
  padding: 20px;
  border-radius: 8px;
  background: var(--bg-card);
}

.master-product-filter-card {
  flex: 0 0 auto;
}

.master-product-filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.master-product-filter-actions {
  margin-right: 0;
}

.master-product-table-card {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.master-product-card-title {
  margin-bottom: 16px;
}

.master-product-card-title h3,
.master-product-card-title p {
  margin: 0;
}

.master-product-card-title h3 {
  font-size: 16px;
}

.master-product-card-title p {
  margin-top: 6px;
  color: var(--nl-color-weak);
  font-size: 13px;
}

.master-product-table-wrap {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.master-product-table-wrap :deep(.el-table) {
  height: 100%;
}

.master-product-column-header {
  display: flex;
  align-items: center;
  min-width: 0;
}

.master-product-column-header :deep(.filter-header-trigger) {
  flex: 1;
  min-width: 0;
}

.master-product-pagination-card {
  display: flex;
  flex: 0 0 auto;
  justify-content: flex-end;
  padding: 12px 24px;
}

.master-product-pagination-card :deep(.pagination-container) {
  margin-top: 0;
}

@media (max-width: 768px) {
  .master-product-card {
    padding: 16px;
  }
}
</style>
