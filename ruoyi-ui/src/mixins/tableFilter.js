// 表格列筛选混入 - 提供 columnSearch、colFilterOptions、filteredDataList
// 配合全局 FilterHeader 组件使用
// filterFieldConfig: { key: { display: (row) => string } } 用于自定义合并字段/展示文本筛选
export default {
  data() {
    return {
      columnSearch: {},
      filterFieldConfig: null,
      // 最终展示给表格的数据，由 refreshFilteredData 实时维护
      filteredDataList: [],
    }
  },
  computed: {
    // 向下兼容：其他页面可能还在用 filteredTableData
    filteredTableData() {
      return this.filteredDataList
    },
    colFilterOptions() {
      const result = {}
      const tableData = this.getSourceData()
      if (!tableData.length) return result
      const props = Object.keys(this.columnSearch)
      props.forEach(prop => {
        const values = tableData
          .map(row => this.getFilterValue(row, prop))
          .filter(value => value !== null && value !== undefined && value !== '')
        result[prop] = [...new Set(values)].map(value => ({ text: String(value), value }))
      })
      return result
    },
  },
  watch: {
    // 用户点击表头筛选时触发
    columnSearch: {
      handler() {
        this.refreshFilteredData()
      },
      deep: true,
    },
    // tableData/tableDataList 更新时自动刷新展示数据
    tableData: {
      handler() {
        this.refreshFilteredData()
      },
    },
    tableDataList: {
      handler() {
        this.refreshFilteredData()
      },
    },
  },
  methods: {
    getSourceData() {
      return this.tableData || this.tableDataList || []
    },
    getFilterValue(row, prop) {
      const config = this.filterFieldConfig && this.filterFieldConfig[prop]
      return config ? config.display(row) : row[prop]
    },
    initColumnSearch(props, customConfig) {
      const s = {}
      props.forEach(p => { s[p] = [] })
      if (customConfig) {
        Object.keys(customConfig).forEach(k => { s[k] = [] })
      }
      this.columnSearch = s
      this.filterFieldConfig = customConfig || null
    },
    // 根据 columnSearch 对原始数据进行筛选
    refreshFilteredData() {
      let data = this.getSourceData()
      for (const [prop, vals] of Object.entries(this.columnSearch)) {
        if (!vals || vals.length === 0) continue
        data = data.filter(row => vals.includes(this.getFilterValue(row, prop)))
      }
      this.filteredDataList = data
    },
    // 供模板调用的筛选变更处理（使用 $set 确保 Vue 2 响应式）
    onColumnFilter(prop, values) {
      this.$set(this.columnSearch, prop, values)
    },
  },
}
