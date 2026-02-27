import { notNull, isNull } from "@/utils"
import { renderMoney } from "@/utils/renderTool"

/**
 * commonTable mixin包含tableHeight计算相关逻辑,table分页，自定义渲染函数，判空函数
 * @param {*} config
 * @returns
 */
export function commonTable(config = {}) {
  if (isNull(config.needWatchTableHeight)) {
    config.needWatchTableHeight = true
  }
  if (isNull(config.defaultOTableHeight)) {
    config.defaultOTableHeight = 403
  }

  const options = {
    data() {
      const data = {
        tableHeight: document.body.clientHeight - config.defaultOTableHeight,
        pages: {
          pageNum: 1,
          pageSize: 30,
          total: 0,
          pageSizes: [30, 50, 100]
        }
      }

      return data
    },
    computed: {
      // bodyHeight() {
      //   return this.$store.state.globalData.bodyHeight
      // }
    },
    filters: {
      renderNoNull(v) {
        return notNull(v) ? v : "-"
      }
    },
    // watch: {
    //   bodyHeight() {
    //     this.tableHeight = this.bodyHeight - config.defaultOTableHeight
    //   }
    // },
    methods: {
      notNull,
      isNull,
      renderMoney,
      // 改变每页条数
      sizeChange(val) {
        this.pages.pageSize = val
        this.pages.pageNum = 1
        this.getDataList()
      },
      // 改变页码
      pageChange(val) {
        this.pages.pageNum = val
        this.getDataList()
      },
      renderMapData(map, val, defaultVal = "-") {
        if (notNull(val) && notNull(map[val])) return map[val].label
        return defaultVal
      },
      renderPropsItemOne(row, key, defaultVal = "-") {
        if (notNull(row)) {
          if (notNull(row[key])) {
            return row[key]
          }
        }
        return defaultVal
      },
      renderPropsItemTwo(row, secondKey, key, defaultVal = "-") {
        if (notNull(row)) {
          if (secondKey) {
            if (notNull(row[secondKey] && row[secondKey][key])) {
              return row[secondKey][key]
            }
          } else {
            if (notNull(row[key])) {
              return row[key]
            }
          }
        }
        return defaultVal
      },
      renderOtherProps(row, item, defaultVal = "-") {
        if (notNull(row)) {
          if (item.extKey) {
            if (notNull(row[item.extKey] && row[item.extKey][item.prop])) {
              return row[item.extKey][item.prop]
            }
          }
          if (notNull(row[item.prop])) {
            return row[item.prop]
          }
        }
        return defaultVal
      },
      /**
       * 渲染序号
       * @param {*} index 当前行索引
       * @returns
       */
      renderSerialNumber(index) {
        return (this.pages.pageNum - 1) * this.pages.pageSize + (index + 1)
      }
    }
  }

  if (!config.needWatchTableHeight) {
    delete options.watch.bodyHeight
    delete options.computed.bodyHeight
  }

  return options
}
