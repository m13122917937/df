import { debounce, setElementOverflow } from "@/utils"
import Vue from "vue"
/**
 * <el-table height="100px" v-table-height="{bottomOffset: 24}">...</el-table>
 * el-table 高度必须设置
 * bottomOffset: 24(default)   // 从页面底部到表格的高度。
 * firstLoaded: boolean 初始化是否计算 元素隐藏初始化计算高度会不对 默认初始化计算 传入false初始化不计算
 */

const doResize = (el, binding, vnode, isResize = false) => {
  if (isResize) setElementOverflow(".page-container")
  // setTimeout(() => {
  Vue.nextTick(() => {
    const { componentInstance: $table } = vnode
    const { value } = binding
    if (!$table.height) {
      throw new Error(`el-$table must set the height. Such as height='100px'`)
    }
    const bottomOffset = (value && value.bottomOffset) || 24
    if (!$table) return

    let height = window.innerHeight - el.getBoundingClientRect().top - bottomOffset
    if (el?.style?.height === `${height}px` || el?.style?.height === `${height - 1}px`) return

    console.log("TableOtherHeight", el.getBoundingClientRect().top + bottomOffset)
    if (height < 150) height = 150
    $table.layout.setHeight(height)
    $table.doLayout()
    // setElementOverflow('.page-container',false)
  })
  // }, 30)
}

export default {
  bind(el, binding, vnode) {
    const { value } = binding
    let { firstLoaded, isLoad = true } = value || {}
    if (!isLoad) return
    el.resizeListener = debounce(() => {
      doResize(el, binding, vnode)
    }, 16)
    el.__expand__ = binding?.value?.expand ?? false
    firstLoaded = firstLoaded ?? true // 不写默认为true
    if (firstLoaded) el.resizeListener()
    window.addEventListener("resize", el.resizeListener, true)
  },
  update(el, binding, vnode) {
    if (el.__expand__ !== binding.value?.expand) {
      el.__expand__ = binding?.value?.expand
      doResize(el, binding, vnode, true)
    }
  },
  unbind(el) {
    window.removeEventListener("resize", el.resizeListener, true)
  }
}
