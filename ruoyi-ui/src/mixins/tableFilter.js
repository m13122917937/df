// 表格列筛选混入 - 提供 columnSearch、colFilterOptions、filteredTableData
// 配合全局 FilterHeader 组件使用
// filterFieldConfig: { key: { display: (row) => string } } 用于自定义合并字段/展示文本筛选
export default {
  data() {
    return {
      columnSearch: {},
      filterFieldConfig: null,
    }
  },
  computed: {
    colFilterOptions() {
      const result = {}
      const tableData = this.tableData || this.tableDataList || []
      if (!tableData.length) return result
      const props = Object.keys(this.columnSearch)
      props.forEach(prop => {
        if (this.filterFieldConfig && this.filterFieldConfig[prop]) {
          const values = [...new Set(tableData.map(r => this.filterFieldConfig[prop].display(r)).filter(v => v !== null && v !== undefined && v !== ''))]
          result[prop] = values.map(v => ({ text: String(v), value: v }))
        } else {
          const values = [...new Set(tableData.map(r => r[prop]).filter(v => v !== null && v !== undefined && v !== ''))]
          result[prop] = values.map(v => ({ text: String(v), value: v }))
        }
      })
      return result
    },
    filteredTableData() {
      let data = this.tableData || this.tableDataList || []
      for (const [prop, vals] of Object.entries(this.columnSearch)) {
        if (!vals || vals.length === 0) continue
        if (this.filterFieldConfig && this.filterFieldConfig[prop]) {
          data = data.filter(row => vals.includes(this.filterFieldConfig[prop].display(row)))
        } else {
          data = data.filter(row => vals.includes(row[prop]))
        }
      }
      return data
    },
  },
  methods: {
    initColumnSearch(props, customConfig) {
      const s = {}
      props.forEach(p => { s[p] = [] })
      if (customConfig) {
        Object.keys(customConfig).forEach(k => { s[k] = [] })
      }
      this.columnSearch = s
      this.filterFieldConfig = customConfig || null
    },
  },
}
