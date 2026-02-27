// directives.js
import _ from "lodash"
export const tableCustomHeight = {
  inserted(el, binding, vnode) {
    const updateTableHeight = _.debounce(function () {
      const {
        offset = 12, // 底部偏移量
        extraSelector = null, // 需要额外减去的元素选择器
        minHeight = 100, // 最小高度
        showTransition = true
      } = binding.value || {}

      const { componentInstance: $table } = vnode

      if ($table && !$table.height) {
        throw new Error(`el-$table must set the height. Such as height='100px'`)
      }

      let totalOffset = offset
      // 计算额外元素高度
      if (extraSelector) {
        const extraEl = document.querySelector(extraSelector)
        if (extraEl) totalOffset += extraEl.offsetHeight
      }
      // 计算表格顶部到视口顶部的距离
      const rect = el.getBoundingClientRect()
      const topOffset = rect.top + window.pageYOffset
      const windowHeight = window.innerHeight
      // 计算最终高度
      let height = windowHeight - topOffset - totalOffset
      height = Math.max(height, minHeight)

      el.style.height = `${height}px`
      if (showTransition) {
        el.style.transition = "height .5s"
      }
      // el.style.overflow= 'hidden'

      if ($table) $table.doLayout()
    }, 10)
    // 初始更新
    updateTableHeight()

    // 监听窗口变化
    window.addEventListener("resize", updateTableHeight)

    // 如果需要监听DOM变化（如布局改变）
    const observer = new MutationObserver(updateTableHeight)
    observer.observe(document.body, {
      childList: true,
      subtree: true,
      attributes: true
    })

    // 保存引用
    el._adaptiveTable = {
      update: updateTableHeight,
      observer
    }
  },
  update(el) {
    // 参数变化时重新计算
    if (el._adaptiveTable) {
      el._adaptiveTable.update()
    }
  },
  unbind(el) {
    if (el._adaptiveTable) {
      window.removeEventListener("resize", el._adaptiveTable.update)
      el._adaptiveTable.observer.disconnect()
    }
  }
}
