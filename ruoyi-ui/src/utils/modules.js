import moment from "moment"

// 获取多少天后的方法 subtract
export const getFutureDays = (day = 0, format = "YYYY-MM-DD") => moment().add(day, "days").format(format)

// 获取多少天前的方法 以前天数
export const getPreviousDays = (day = 0, format = "YYYY-MM-DD") => moment().subtract(day, "days").format(format)

// 获取状态label
export const getDataLabel = (list, val) => (list || []).find(e => e.value == val)?.label || "-"

// 获取两个时间相差天数

export const differenceInDays = (date1, date2) => {
  return moment(date2).diff(moment(date1), "days")
}

// 弹窗过高增加滚动
export const setDialogHeight = (vm, className, { isFooter, maxHeight } = {}) => {
  isFooter = isFooter ?? true
  maxHeight = maxHeight ?? 0
  vm.$nextTick(() => {
    const dom = document.querySelector(`${className} .el-dialog__body`)
    const otherHeight = isFooter ? 165 : 92
    const height = Math.floor(document.documentElement.clientHeight - otherHeight)
    dom.style.height = `${height > maxHeight && maxHeight > 0 ? maxHeight : height}px`
    dom.style.overflowY = "auto"
    dom.style.overflowX = "none"
  })
}

export function timeData(data) {
  data = data ?? {}
  return { ...data, timeStr: Date.now() }
}

// 支持负数的千分位
export function toThousandsNum(amount) {
  let amountStr = amount + ""
  let isNum = /^[\d.-]+$/
  if (!isNum.test(amountStr)) return amount
  let negative = amount < 0
  if (negative) amountStr = amountStr.slice(1)
  let amountParts = amountStr.split(".")
  let integer = amountParts[0].replace(/(?!^)(?=(\d{3})+$)/g, ",")
  let decimal = amountParts[1] || ""
  if (negative) {
    return "-" + (decimal ? integer + "." + decimal : integer)
  }
  return decimal ? integer + "." + decimal : integer
}

// 是否大于xx的数字 显示xx+
// 默认99 默认符号+
export function greaterThanNumber(num, defaultNum = 99, unit = "+") {
  if (!num) return num
  if (Number.isNaN(+num)) return num
  if (num > defaultNum) return `${defaultNum}${unit}`
  return num
}

// 输入整数 不能为0
export const numberRegVal = value => value.replace(/^(0+)|[^\d]+/g, "")

// 输入整数 可以为0
export const numberZeroRegVal = value => value.replace(/[^\d]+/g, "")

// 输入整数 不能超过最大值 不能小于最小值
export const numberRegCountVal = (value, min, max, options) => {
  const { isClear = false } = options || {}
  if (isClear && !value) return ""
  let val = value.replace(/[^\d]+/g, "")
  val = Number(val)
  if (val <= min) return min
  if (max) {
    if (val >= max) return max
  }
  return val
}

// 输入整数 不能超过最大值 不能小于最小值
export const numberRegNegativeVal = (value, min, max) => {
  if (!value) return ""
  if (value === "-") return "-"
  let val = value.replace(/[^-\d]+/g, "").replace(/-+$/, "")
  val = Number(val)
  if (val <= min) return min
  if (max) {
    if (val >= max) return max
  }
  return val
}

// 限制为两位小数  不能超过最大值 不能小于最小值
export const numberRegDecimalNum = (value, min, max) => {
  if (!value) return ""
  let val = numberRegDecimalVal(value)
  let numVal = Number(val)
  // if(numVal === 0) return '';
  if (numVal <= min) return min
  if (max) {
    if (numVal >= max) return max
  }
  return val
}
// 限制为两位小数
export const numberRegDecimalVal = value =>
  ("" + value) // 第一步：转成字符串
    .replace(/[^\d.]+/g, "") // 第二步：把不是数字，不是小数点的过滤掉
    .replace(/^0+(\d)/, "$1") // 第三步：第一位0开头，0后面为数字，则过滤掉，取后面的数字
    .replace(/^\./, "0.") // 第四步：如果输入的第一位为小数点，则替换成 0. 实现自动补全
    .match(/^\d*(\.?\d{0,2})/g)[0] || "" // 第五步：最终匹配得到结果 以数字开头，只有一个小数点，而且小数点后面只能有0到2位小数

// 处理大于万的数字
export function toMoney(num, toFixedNum = 2) {
  if (Number(num) < 9999) return num
  let countNum = num
  let val
  //转换数字
  if (countNum > 9999) {
    val = (countNum / 10000).toFixed(toFixedNum) + "万"
  }
  // if (countNum > 99999999) {
  //   val = (countNum / 100000000).toFixed(toFixedNum) + "亿";
  // }
  return val
}

// 防抖函数
export function debounce(func, wait) {
  let timeout
  return function () {
    const context = this
    const args = arguments
    clearTimeout(timeout)
    timeout = setTimeout(() => {
      func.apply(context, args)
    }, wait)
  }
}

export const cubic = value => Math.pow(value, 3)
export const easeInOutCubic = value => (value < 0.5 ? cubic(value * 2) / 2 : 1 - cubic((1 - value) * 2) / 2)
