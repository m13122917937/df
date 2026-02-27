export default {
  inserted: function (el, binding) {
    el.scrollTimer = null
    const defaultDiff = { top: 0, bottom: 30 }

    const config = binding.value
    const callback = typeof config === "function" ? config : config.callback
    if (!["function"].includes(typeof callback)) throw new Error("need a Function or Object with callback Function")
    const diff = { top: config?.diff?.top ?? defaultDiff.top, bottom: config?.diff?.bottom ?? defaultDiff.bottom }
    const repeatTime = config?.repeatTime ?? 100

    const _el = el.classList.contains("el-table") ? el.querySelector(".el-table__body-wrapper") : el

    _el._scrollHandler = function () {
      if (el.scrollTimer) clearTimeout(el.scrollTimer)

      // 计算元素内容总高度、可视高度及滚动位置
      const contentHeight = _el.scrollHeight
      const viewportHeight = _el.clientHeight
      const scrollPosition = _el.scrollTop

      const bottom = contentHeight - scrollPosition - viewportHeight
      const returns = {
        top: scrollPosition,
        bottom,
        isStart: scrollPosition <= diff.top,
        isEnd: bottom <= diff.bottom
      }
      el.scrollTimer = setTimeout(() => {
        callback(returns)
      }, repeatTime)
    }
    _el.addEventListener("scroll", _el._scrollHandler)
  },
  unbind(el) {
    if (el.scrollTimer) {
      clearTimeout(el.scrollTimer)
      delete el.scrollTimer
    }
    const _el = el.classList.contains("el-table") ? el.querySelector(".el-table__body-wrapper") : el
    if (_el._scrollHandler) {
      _el.removeEventListener("scroll", _el._scrollHandler)
      delete _el._scrollHandler
    }
  }
}
