export default {
  data() {
    return {
      // 分页信息
      page: {
        pageNum: 1,
        pageSize: 30,
        total: 0,
        pageSizes: [30, 50, 100]
      }
    }
  },
  computed: {
    // 获取页码信息 排除总数量 pageSizes
    getPageInfo() {
      const { pageNum, pageSize } = this.page
      return { pageNum, pageSize }
    }
  },
  watch: {
    "page.total": function (total) {
      const totalPage = Math.ceil(total / this.page.pageSize)
      if (this.page.pageNum > totalPage && total > 0) {
        this.page.pageNum = totalPage
        this.getData()
      }
    }
  },
  methods: {
    //  getData 查询列表
    // 页码改变
    pageCurrentChange(pageNum) {
      this.page.pageNum = pageNum
      this.getData()
    },
    // 删除更新操作
    checkValidatePageNum(num) {
      if (Math.ceil((this.page.total - num) / this.page.pageSize) < this.page.pageNum) {
        this.page.pageNum = this.page.pageNum - 1 ? this.page.pageNum - 1 : 1
        this.page.total = this.page.total - num ? this.page.total - num : 0
      }
      return Math.ceil((this.page.total - num) / this.page.pageSize) < this.page.pageNum
    },
    // 每页数量改变
    pageSizeChange(size) {
      if (this.page.pageNum * size > this.page.total) {
        this.page.pageNum = 1
      }
      this.page.pageSize = size
      this.getData()
    },
    // 获取当前表格序号
    tableIndexNum(index) {
      const { pageNum, pageSize } = this.getPageInfo
      if (pageNum) {
        return Math.floor((pageNum - 1) * pageSize + index + 1)
      }
      return Math.floor(index + 1)
    },
    // 全选表格
    toggleSelection(ref = "table") {
      this.clearSelection(ref)
      this.$nextTick(() => {
        this.$refs[ref].toggleAllSelection()
      })
    },
    // 全选后端选中的表格
    toggleSelectSelection(rows, ref = "table") {
      if ((rows || []).length) {
        this.$nextTick(() => {
          rows.forEach(row => {
            this.$refs[ref].toggleRowSelection(row)
          })
        })
      }
    },
    // 清空选中表格
    clearSelection(ref = "table") {
      this.$nextTick(() => {
        this.$refs[ref].clearSelection()
      })
    }
  }
}
