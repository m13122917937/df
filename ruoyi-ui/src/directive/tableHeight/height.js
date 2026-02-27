import { domComputed, windowResize } from "@/utils/dom/index.js"
import { debounce } from "@/utils/index.js"

export default {
  bind(el, binding) {
    el._getHeight = debounce(() => {
      const height = domComputed.getHeight({ target: el, bottom: binding.value?.bottom ?? 24 })
      console.log("tableheight", height)
      el.style.height = height + "px"
    }, 16)
    windowResize.addListener(el._getHeight)
  },
  update(el) {
    el._getHeight()
  },
  inserted(el) {
    el._getHeight()
  },
  unbind(el) {
    windowResize.removeListener(el._getHeight)
  }
}
