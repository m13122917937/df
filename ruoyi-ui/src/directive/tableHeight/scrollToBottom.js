import { throttle } from "@/utils"
/**
 *
 * el-table 高度必须设置
 * requestFn   // 请求函数。
 * bottom // 距离底部多少距离请求下一页
 */

const bottomFn = throttle(function (requestFn) {
  requestFn.apply(this, arguments)
}, 300)

const loadMore = (el, binding, vnode) => {
  const { componentInstance: $table } = vnode
  const { requestFn, bottom = 30 } = (binding || {}).value || {}

  if (!$table.height) {
    throw new Error(`el-$table must set the height. Such as height='100px'`)
  }
  if (!$table) return
  const scrollEl = $table.bodyWrapper
  let clientHeight = scrollEl.clientHeight
  let scrollTop = scrollEl.scrollTop
  let scrollHeight = scrollEl.scrollHeight
  const offsetHeight = clientHeight + scrollTop + bottom
  if (offsetHeight > scrollHeight) {
    bottomFn(requestFn)
  }
}

export default {
  inserted(el, binding, vnode) {
    const { componentInstance: $table } = vnode
    const scrollEl = $table.bodyWrapper
    el.scrollListener = () => loadMore(el, binding, vnode)
    scrollEl.addEventListener("scroll", el.scrollListener)
  },
  unbind(el, binding, vnode) {
    const { componentInstance: $table } = vnode
    const scrollEl = $table.bodyWrapper
    scrollEl.removeEventListener("scroll", el.scrollListener)
  }
}
