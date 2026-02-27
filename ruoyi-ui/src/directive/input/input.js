// 整数
const integerInput = value => {
  return value.replace(/^(0+)|[^\d]+/g, "")
}
// 整数 和换行符
const integerNInput = value => {
  return value.replace(/[^0-9\n]/g, "")
}
// 保留两位小数
export const decimalInput = value => {
  return (
    ("" + value) // 第一步：转成字符串
      .replace(/[^\d.]+/g, "") // 第二步：把不是数字，不是小数点的过滤掉
      .replace(/^0+(\d)/, "$1") // 第三步：第一位0开头，0后面为数字，则过滤掉，取后面的数字
      .replace(/^\./, "0.") // 第四步：如果输入的第一位为小数点，则替换成 0. 实现自动补全
      .match(/^\d*(\.?\d{0,2})/g)[0] || ""
  )
}
// 整数包含0
const integerInputZero = value => {
  return value.replace(/[^\d]+/g, "").replace(/^0+(\d)/, "$1")
}
// 规则枚举
const regMapData = {
  integerZero: integerInputZero,
  integer: integerInput,
  integerN: integerNInput,
  decimal: decimalInput
}

// input 事件
const input = function (ipt, type, vnode, e) {
  if (vnode.locking) return
  ipt.value = regMapData[type](ipt.value)
  if (vnode.paste || e?.detail?.isLocking === true) {
    if (e?.detail?.isLocking) e.detail.isLocking = false
    if (vnode.paste) vnode.paste = false
    e.target.dispatchEvent(new InputEvent("input"))
  }
}
// 开始中文输入法
function compositionstart(vnode) {
  vnode.locking = true
}
// 结束中文输入法
function compositionend(e, vnode) {
  vnode.locking = false
  e.target.dispatchEvent(new CustomEvent("input", { detail: { isLocking: true } }))
}

// 粘贴先执行 方法
function paste(vnode) {
  vnode.paste = true
}
const EventRecordMap = new WeakMap()
export default {
  bind(el, binding, vnode) {
    const { value } = binding
    let { type } = value || {}
    type = type || "integer"
    // 如果不存在这个type类型 默认赋值为整数
    if (!regMapData[type]) type = "integer"
    const iptEl = el.querySelector(".el-input__inner") || el.querySelector(".el-textarea__inner") || el
    // 存储dom绑定的事件
    EventRecordMap.set(iptEl, [iptEl, compositionstart, compositionend, paste])
    // 中文输入事件处理
    iptEl.addEventListener("compositionstart", () => compositionstart(vnode))
    iptEl.addEventListener("compositionend", e => compositionend(e, vnode))
    // 粘贴操作
    iptEl.addEventListener("paste", () => paste(vnode))
    // input事件执行
    iptEl.addEventListener("input", e => input(iptEl, type, vnode, e))
  },
  unbind(el) {
    // 卸载事件
    const input = el.querySelector(".el-input__inner") || el.querySelector(".el-textarea__inner") || el
    if (!input) return
    if (EventRecordMap.has(input)) {
      const events = EventRecordMap.get(input)
      input.removeEventListener("input", events[0])
      input.removeEventListener("compositionstart", events[1])
      input.removeEventListener("compositionend", events[2])
      input.removeEventListener("paste", events[3])
    }
  }
}
