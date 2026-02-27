export default {
  inserted(el, binding) {
    // 获取 element-ui 定义好的 scroll 盒子
    const SELECTDOWN_DOM = el.querySelector(".el-select-dropdown .el-select-dropdown__wrap")
    SELECTDOWN_DOM.addEventListener("scroll", function () {
      const CONDITION = this.scrollHeight - this.scrollTop <= this.clientHeight
      if (CONDITION) {
        binding.value()
      }
    })
  }
}
