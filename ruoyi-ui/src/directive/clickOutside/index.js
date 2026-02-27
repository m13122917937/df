export default {
  bind(el, binding, vnode) {
    el._clickOutsideEvent = function (event) {
      if (!(el === event.target || el.contains(event.target))) {
        vnode.context[binding.expression](event)
      }
    }
    document.body.addEventListener("click", el._clickOutsideEvent)
  },
  unbind(el) {
    document.body.removeEventListener("click", el._clickOutsideEvent)
  }
}
