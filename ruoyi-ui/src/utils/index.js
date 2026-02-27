import moment from "moment"
import { parseTime } from "./ruoyi"
/**
 * 表格时间格式化
 */
export const formatDate = (date, fmt) => {
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, String(date.getFullYear()).substr(4 - RegExp.$1.length))
  }
  let o = {
    "M+": date.getMonth() + 1,
    "d+": date.getDate(),
    "h+": date.getHours(),
    "m+": date.getMinutes(),
    "s+": date.getSeconds()
  }
  for (let k in o) {
    if (new RegExp(`(${k})`).test(fmt)) {
      let str = String(o[k])
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length === 1 ? str : padLeftZero(str))
    }
  }
  return fmt
}

function padLeftZero(str) {
  return ("00" + str).substr(str.length)
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime(time, option) {
  if (("" + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return "刚刚"
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + "分钟前"
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + "小时前"
  } else if (diff < 3600 * 24 * 2) {
    return "1天前"
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return d.getMonth() + 1 + "月" + d.getDate() + "日" + d.getHours() + "时" + d.getMinutes() + "分"
  }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function getQueryObject(url) {
  url = url == null ? window.location.href : url
  const search = url.substring(url.lastIndexOf("?") + 1)
  const obj = {}
  const reg = /([^?&=]+)=([^?&=]*)/g
  search.replace(reg, (rs, $1, $2) => {
    const name = decodeURIComponent($1)
    let val = decodeURIComponent($2)
    val = String(val)
    obj[name] = val
    return rs
  })
  return obj
}

/**
 * @param {string} input value
 * @returns {number} output value
 */
export function byteLength(str) {
  // returns the byte length of an utf8 string
  let s = str.length
  for (var i = str.length - 1; i >= 0; i--) {
    const code = str.charCodeAt(i)
    if (code > 0x7f && code <= 0x7ff) s++
    else if (code > 0x7ff && code <= 0xffff) s += 2
    if (code >= 0xdc00 && code <= 0xdfff) i--
  }
  return s
}

/**
 * @param {Array} actual
 * @returns {Array}
 */
export function cleanArray(actual) {
  const newArray = []
  for (let i = 0; i < actual.length; i++) {
    if (actual[i]) {
      newArray.push(actual[i])
    }
  }
  return newArray
}

/**
 * @param {Object} json
 * @returns {Array}
 */
export function param(json) {
  if (!json) return ""
  return cleanArray(
    Object.keys(json).map(key => {
      if (json[key] === undefined) return ""
      return encodeURIComponent(key) + "=" + encodeURIComponent(json[key])
    })
  ).join("&")
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj(url) {
  const search = decodeURIComponent(url.split("?")[1]).replace(/\+/g, " ")
  if (!search) {
    return {}
  }
  const obj = {}
  const searchArr = search.split("&")
  searchArr.forEach(v => {
    const index = v.indexOf("=")
    if (index !== -1) {
      const name = v.substring(0, index)
      const val = v.substring(index + 1, v.length)
      obj[name] = val
    }
  })
  return obj
}

/**
 * @param {string} val
 * @returns {string}
 */
export function html2Text(val) {
  const div = document.createElement("div")
  div.innerHTML = val
  return div.textContent || div.innerText
}

/**
 * Merges two objects, giving the last one precedence
 * @param {Object} target
 * @param {(Object|Array)} source
 * @returns {Object}
 */
export function objectMerge(target, source) {
  if (typeof target !== "object") {
    target = {}
  }
  if (Array.isArray(source)) {
    return source.slice()
  }
  Object.keys(source).forEach(property => {
    const sourceProperty = source[property]
    if (typeof sourceProperty === "object") {
      target[property] = objectMerge(target[property], sourceProperty)
    } else {
      target[property] = sourceProperty
    }
  })
  return target
}

/**
 * @param {HTMLElement} element
 * @param {string} className
 */
export function toggleClass(element, className) {
  if (!element || !className) {
    return
  }
  let classString = element.className
  const nameIndex = classString.indexOf(className)
  if (nameIndex === -1) {
    classString += "" + className
  } else {
    classString = classString.substr(0, nameIndex) + classString.substr(nameIndex + className.length)
  }
  element.className = classString
}

/**
 * @param {string} type
 * @returns {Date}
 */
export function getTime(type) {
  if (type === "start") {
    return new Date().getTime() - 3600 * 1000 * 24 * 90
  } else {
    return new Date(new Date().toDateString())
  }
}

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce(func, wait, immediate) {
  let timeout, context, timestamp, result
  const later = function (args) {
    // 据上一次触发时间间隔
    const last = +new Date() - timestamp

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args)
        if (!timeout) context = args = null
      }
    }
  }

  return function (...args) {
    context = this
    timestamp = +new Date()
    const callNow = immediate && !timeout
    // 如果延时不存在，重新设定延时
    if (!timeout) timeout = setTimeout(() => later(args), wait)
    if (callNow) {
      result = func.apply(context, args)
      context = args = null
    }

    return result
  }
}

/**
 * This is just a simple version of deep copy
 * Has a lot of edge cases bug
 * If you want to use a perfect deep copy, use lodash's _.cloneDeep
 * @param {Object} source
 * @returns {Object}
 */
export function deepClone(source) {
  if (!source && typeof source !== "object") {
    throw new Error("error arguments", "deepClone")
  }
  const targetObj = source.constructor === Array ? [] : {}
  Object.keys(source).forEach(keys => {
    if (source[keys] && typeof source[keys] === "object") {
      targetObj[keys] = deepClone(source[keys])
    } else {
      targetObj[keys] = source[keys]
    }
  })
  return targetObj
}

/**
 * @param {Array} arr
 * @returns {Array}
 */
export function uniqueArr(arr) {
  return Array.from(new Set(arr))
}

/**
 * @returns {string}
 */
export function createUniqueString() {
  const timestamp = +new Date() + ""
  const randomNum = parseInt((1 + Math.random()) * 65536) + ""
  return (+(randomNum + timestamp)).toString(32)
}

/**
 * Check if an element has a class
 * @param {HTMLElement} elm
 * @param {string} cls
 * @returns {boolean}
 */
export function hasClass(ele, cls) {
  return !!ele.className.match(new RegExp("(\\s|^)" + cls + "(\\s|$)"))
}

/**
 * Add class to element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function addClass(ele, cls) {
  if (!hasClass(ele, cls)) ele.className += " " + cls
}

/**
 * Remove class from element
 * @param {HTMLElement} elm
 * @param {string} cls
 */
export function removeClass(ele, cls) {
  if (hasClass(ele, cls)) {
    const reg = new RegExp("(\\s|^)" + cls + "(\\s|$)")
    ele.className = ele.className.replace(reg, " ")
  }
}

export function makeMap(str, expectsLowerCase) {
  const map = Object.create(null)
  const list = str.split(",")
  for (let i = 0; i < list.length; i++) {
    map[list[i]] = true
  }
  return expectsLowerCase ? val => map[val.toLowerCase()] : val => map[val]
}

export const exportDefault = "export default "

export const beautifierConf = {
  html: {
    indent_size: "2",
    indent_char: " ",
    max_preserve_newlines: "-1",
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: "separate",
    brace_style: "end-expand",
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: false,
    end_with_newline: true,
    wrap_line_length: "110",
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true
  },
  js: {
    indent_size: "2",
    indent_char: " ",
    max_preserve_newlines: "-1",
    preserve_newlines: false,
    keep_array_indentation: false,
    break_chained_methods: false,
    indent_scripts: "normal",
    brace_style: "end-expand",
    space_before_conditional: true,
    unescape_strings: false,
    jslint_happy: true,
    end_with_newline: true,
    wrap_line_length: "110",
    indent_inner_html: true,
    comma_first: false,
    e4x: true,
    indent_empty_lines: true
  }
}

// 首字母大小
export function titleCase(str) {
  return str.replace(/( |^)[a-z]/g, L => L.toUpperCase())
}

// 下划转驼峰
export function camelCase(str) {
  return str.replace(/_[a-z]/g, str1 => str1.substr(-1).toUpperCase())
}

export function isNumberStr(str) {
  return /^[+-]?(0|([1-9]\d*))(\.\d+)?$/g.test(str)
}

/**
 * 校验输入价格
 */
export function isPrice(val) {
  let str = val
  let len1 = str.substr(0, 1)
  let len2 = str.substr(1, 1)
  //如果第一位是0，第二位不是点，就用数字把点替换掉
  if (str.length > 1 && len1 == 0 && len2 !== ".") {
    str = str.substr(1, 1)
  }
  //第一位不能是.
  if (len1 === ".") {
    str = ""
  }
  //限制只能输入一个小数点
  if (str.indexOf(".") !== -1) {
    let str_ = str.substr(str.indexOf(".") + 1)
    if (str_.indexOf(".") !== -1) {
      str = str.substr(0, str.indexOf(".") + str_.indexOf(".") + 1)
    }
  }
  //校验 保留数字和两位小数点
  str = str.match(/^\d*(\.?\d{0,2})/g)[0] || null
  return str
}

/**
 * 判断是否为空
 */
export function isEmpty(val) {
  if (typeof val === "boolean") {
    return false
  }
  if (typeof val === "number") {
    return false
  }
  if (val instanceof Array) {
    if (val.length === 0) return true
  } else if (val instanceof Object) {
    if (JSON.stringify(val) === "{}") return true
  } else {
    if (val === "null" || val == null || val === "undefined" || val === undefined || val === "") return true
    return false
  }
  return false
}

// // 数字千位符格式化
// export function toThousandsNum(val) {
//   let num = (val || 0).toString()
//   let result = ''
//
//   while (parseInt(num).toString().length > 3) {
//       //此处用数组的slice方法，如果是负数，那么它规定从数组尾部开始算起的位置
//       result = ',' + num.slice(-3) + result;
//       num = num.slice(0, num.length - 3);
//   }
//   // 如果数字的开头为0,不需要逗号
//   if (num){
//     result = num + result
//   }
//   return result;
// }

// 数字千位符格式化
export function toThousandsNum(amount) {
  let amountStr = amount + ""
  let isNum = /^[\d.]+$/
  if (!isNum.test(amountStr)) return amount

  let amountParts = amountStr.split(".")
  let integer = amountParts[0].replace(/(?!^)(?=(\d{3})+$)/g, ",")
  let decimal = amountParts[1] || ""
  return decimal ? integer + "." + decimal : integer
}

// 价格格式
export function showPrice(price, num = 2, isInteger = false) {
  if (!price) return "￥0"
  if (["string", "number"].includes(typeof price)) {
    if (isNaN(+price)) return price
    // 整数是否显示小数位 默认不显示
    if (!Number.isInteger(+price) && !isInteger) {
      price = (+price).toFixed(num)
    }
    // if(Number.isInteger(+price)&&isInteger) price = (+price).toFixed(0);
    let temp = !(+price + "").includes(".")
      ? (price + "").replace(/\d{1,3}(?=(\d{3})+$)/g, match => {
        return match + ","
      })
      : (price + "").replace(/\d{1,3}(?=(\d{3})+(\.))/g, match => {
        return match + ","
      })
    return "￥" + temp
  }
}

// 账期
export function accountingPeriod(status) {
  return "T+" + status
}
// 发货时效
export function deadline(val, type = "delivery") {
  if (val === -1) return type === "delivery" ? "已过发货时效" : "已过到货时效"
  if (val === 0) return "当日 24:00"
  if (val === 1) return "次日 24:00"
  if (val === 2) return "后日 24:00"
  if (val > 2) return `${val + 1}日内`
  return "-"
}
// 串码信息
export function auditStatus(val) {
  if (val === 1) return "已通过"
  if (val === 2) return "已拒绝"
  if (val === 3) return "待审核"
  return ""
}

// 异常原因
export function orderSubStatus(val) {
  if (val == 121) return "物流目的地异常"
  if (val == 122) return "物流发货城市异常"
  if (val == 123) return "物流无流转信息"
  if (val == 124) return "签收异常"
  if (val === 125) return "物流疑难"
  if (val === 126) return "7天未签收异常"
  if (val === 127) return "手机号后4位错误"
  if (val === 128) return "揽收异常"
  if (val === 91) return "拼多多已销售"
  if (val === 92) return "已激活"
  if (val === 93) return "串码错误"
  if (val === 94) return "已发商品与订单要求不一致"
  if (val === 95) return "序列号不存在"
  if (val === 96) return "型号不一致"
  if (val === 97) return "串码异常"
  return val
}

// 省市
export function areaName(province, city) {
  if (province && city) {
    if (province.includes(city) || city.includes(province)) return province
    return province + " " + city
  }
  if (province) return province
  if (city) return city
  return ""
}

// 税票要求
export function taxRequire(val) {
  if (val == 1) return "含税（型号对应）"
  if (val == 2) return "含税"
  if (val == 3) return "无需税票"
  return "无需税票"
}

/**
 * 获取小数点位数
 *  */
function getPrecision(num) {
  return num.toString().split(".")[1]?.length || 0
}
function operateNumbers(operation, ...nums) {
  if (nums.length === 0) throw new Error("参数不能为空")
  if (nums.some(item => isNaN(item))) throw new Error("非法参数，参数必须为数字或数字字符串")
  if (nums.length === 1) return +nums[0]
  return operation()
}

/**
 * @description 加法运算，避免数据相加因精度产生错误。
 * @param { ...(Number | string) } nums 可变数量的数字或数字字符串
 * @returns { Number } 加法运算结果
 */
export function add(...nums) {
  return operateNumbers(() => {
    const precision = Math.pow(10, Math.max(...nums.map(getPrecision)))
    return nums.reduce((total, item) => total + Math.round(item * precision), 0) / precision
  }, ...nums)
}

/**
 * @description 减法运算，避免数据相减因精度产生错误。
 * @param { ...(Number | string) } nums 可变数量的数字或数字字符串
 * @returns { Number } 减法运算结果
 */
export function sub(...nums) {
  return operateNumbers(() => {
    const precision = Math.pow(10, Math.max(...nums.map(getPrecision)))
    const [num1, ...otherNums] = nums
    return Math.round(num1 * precision - otherNums.reduce((total, item) => total + Math.round(item * precision), 0)) / precision
  }, ...nums)
}
/**
 * @description 乘法运算，避免数据相乘因精度产生错误。
 * @param {Number | string } a
 * @param {Number | string } b
 * @returns { Number } 乘法运算结果
 */
export function mul(a, b) {
  if (isNaN(a) || isNaN(b)) throw new Error("非法参数，参数为两个数字或数字字符串")
  const aPrecision = getPrecision(a)
  const bPrecision = getPrecision(b)
  return Math.round(a * Math.pow(10, aPrecision) * Math.round(b * Math.pow(10, bPrecision))) / Math.pow(10, aPrecision + bPrecision)
}

/**
 * @description 除法运算，避免数据相除因精度产生错误。
 * @param {Number | string } a
 * @param {Number | string } b
 * @returns { Number } 除法运算结果
 */
export function div(a, b) {
  if (isNaN(a) || isNaN(b)) throw new Error("非法参数，参数为两个数字或数字字符串")
  if (b === 0) throw new Error("被除数不能为0")
  const aPrecision = getPrecision(a)
  const bPrecision = getPrecision(b)
  return mul(Math.round(a * Math.pow(10, aPrecision)) / Math.round(b * Math.pow(10, bPrecision)), Math.pow(10, bPrecision - aPrecision))
}

export function getDomBoundingClientRect(el) {
  return el.getBoundingClientRect() || {}
}

export function getDomDeleteTopHeight(el, deleteHeight = 12, bodyHeight) {
  const height = bodyHeight || document.documentElement.clientHeight
  const { top } = getDomBoundingClientRect(el)
  const domHeight = height - top - deleteHeight > 0 ? height - top - deleteHeight : 0
  return domHeight
}

// 节流
export function throttle(fn, delay, isImmediate = true) {
  let last = Date.now()
  return function () {
    let now = Date.now()
    if (isImmediate) {
      fn.apply(this, arguments)
      isImmediate = false
      last = now
    }
    if (now - last >= delay) {
      fn.apply(this, arguments)
      last = now
    }
  }
}

// 动态加载script资源 Promise
export function loadScript(url) {
  return new Promise((resolve, reject) => {
    const script = document.createElement("script")
    script.type = "text/javascript"
    if (script.readyState) {
      // IE浏览器
      script.onreadystatechange = function () {
        if (script.readyState === "loaded" || script.readyState === "complete") {
          script.onreadystatechange = null // 删除事件，避免重复触发
          resolve()
        }
      }
    } else {
      script.onload = function () {
        resolve()
      }
      script.onerror = function () {
        reject(new Error("Fail to load: " + url))
      }
    }
    script.src = url
    document.body.appendChild(script)
  })
}

export function isNull(v) {
  return v === undefined || v === null || v === ""
}

export function notNull(v) {
  return v !== undefined && v !== null && v !== ""
}

export function valueOrDefault(v, defaultVal = "") {
  return notNull(v) ? v : defaultVal
}

export function numberOrDefault(v, defaultVal = "") {
  return notNull(v) ? Number(v) : defaultVal
}

export function notNullArray(v) {
  return notNull(v) && Array.isArray(v) && v.length
}

// export function updateLoginInfoTag() {
//   let times = new Date().getTime()
//   let localTime = localStorage.getItem('loginTagTime');
//   localTime = localTime ? localTime * 1 : localTime
//   function load() {
//     updateLifinInfoApi(1).then(([res]) => {
//       if (res) {
//         let nextTime = moment(times + 1000 * 60 * 60 * 24).format("YYYY-MM-DD")
//         localStorage.setItem('loginTagTime', moment(nextTime).valueOf())
//       }
//     })
//   }
//   // 没有则获取信息
//   if (!localTime || times >= localTime) {
//     load();
//   }
// }
// 如何判断当前环境是企业微信
export function envjudge() {
  var isMobile = window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i) // 是否手机端
  var isWx = /micromessenger/i.test(navigator.userAgent) // 是否微信
  var isComWx = /wxwork/i.test(navigator.userAgent) // 是否企业微信
  if (isComWx && isMobile) {
    //手机端企业微信
    return "com-wx-mobile"
  } else if (isComWx && !isMobile) {
    //PC端企业微信
    return "com-wx-pc"
  } else if (isWx && isMobile) {
    // 手机端微信
    return "wx-mobile"
  } else if (isWx && !isMobile) {
    // PC端微信
    return "wx-pc"
  } else {
    return "other"
  }
}

export function getTextWidth(str) {
  let width = 0
  let html = document.createElement("span")
  html.innerText = str
  html.className = "getTextWidth"
  document.querySelector("body").appendChild(html)
  width = document.querySelector(".getTextWidth").offsetWidth
  if (isIE() || isIE11()) {
    document.querySelector(".getTextWidth").removeNode(this)
  } else {
    document.querySelector(".getTextWidth").remove()
  }
  function isIE() {
    if (!!window.ActiveXObject || "ActiveXObject" in window) {
      return true
    } else {
      return false
    }
  }
  function isIE11() {
    if (/Trident\/7\./.test(navigator.userAgent)) {
      return true
    } else {
      return false
    }
  }
  return width
}

// 返回query指定参数 并在query中删除
export function getQueryParams(that, arr) {
  arr = typeof arr === "string" ? [arr] : arr
  let route = that.$route
  let query = route.query
  let oldQuery = JSON.stringify(query)
  let queryObj = {}
  let queryStr = ""

  for (let i in arr) {
    let str = arr[i]
    if (query[str]) {
      queryObj[str] = query[str]
      that.$delete(query, str)
    }
  }
  for (let key in query) queryStr = (queryStr ? "&" : "?") + `${key}=${query[key]}`
  if (JSON.stringify(query) != oldQuery) {
    let url = route.path + queryStr
    window.history.replaceState(null, null, url)
  }

  return queryObj
}

// 帐期
export function accountingPeriodValueText(item, isLaborServices) {
  const { brand, accountingPeriodValue } = item || {}
  if (isLaborServices && (brand === "劳务" || brand === "耗材")) {
    return `每月${accountingPeriodValue}日`.replace(/a/g, "")
  }
  return accountingPeriodValue === "-1" ? "入仓秒款" : `T+${accountingPeriodValue || accountingPeriodValue === 0 ? accountingPeriodValue : ""}`
}

// 输入整数 不能为0
export const numberRegVal = value => value.replace(/^(0+)|[^\d]+/g, "")
// 输入整数 不能超过最大值 不能小于最小值
export const numberRegCountVal = (value, min, max, space = false) => {
  if (space && !value) return ""
  let val = value.replace(/[^\d]+/g, "")
  val = Number(val)
  if (val <= min) return min
  if (max) {
    if (val >= max) return max
  }
  return val
}
// 限制为小数  不能超过最大值 不能小于最小值
export const numberRegDecimalNum = (value, min, max, space = false, decimalPlaces = 2, allowNegative = false) => {
  if (space && !value) return ""
  let val = numberRegDecimalVal(value, decimalPlaces, allowNegative)
  if (val <= min) return min
  if (max) {
    if (val >= max) return max
  }
  return val
}
// 限制为n位小数
export const numberRegDecimalVal = (value, decimalPlaces = 2, allowNegative = false) => {
  const negativePattern = allowNegative ? "-?" : ""
  const regex = new RegExp(`^${negativePattern}\\d*(\\.?\\d{0,${decimalPlaces}})`, "g")
  const val =
    ("" + value) // 第一步：转成字符串
      .replace(new RegExp(`[^\\d.${allowNegative ? "-" : ""}]+`, "g"), "") // 第二步：根据配置过滤负号
      .replace(/^(-?)0+(\d)/, "$1$2") // 第三步：处理负号后的前导零
      .replace(/^(-?)\./, "$10.") // 第四步：处理负号后的小数点
      .match(regex)[0] || "" // 第五步：最终匹配得到结果
  return val
}
// 根据某个key值合并去重
export const mergeAndUniqueByKey = (arr1, arr2, key) => {
  return [...arr1, ...arr2].reduce((acc, item) => {
    if (!acc.some(accItem => accItem[key] === item[key])) {
      acc.push(item)
    }
    return acc
  }, [])
}
// 获取 送货上门与快递发货仓库合集
export const getWarehouseAddressList = item => {
  const { warehouseReceivingAddressList = [], warehouseReceivingAddressLogisticList = [], collectivePurchaseType } = item
  let allAddressList = warehouseReceivingAddressList
  if ([4].includes(collectivePurchaseType)) {
    allAddressList = mergeAndUniqueByKey(warehouseReceivingAddressList, warehouseReceivingAddressLogisticList, "id")
  }
  return allAddressList
}
// 深度合并对象
function isObject(obj) {
  return obj != null && typeof obj === "object"
}

export function deepMerge(target, ...sources) {
  if (!sources.length) return target

  const merge = (target, source) => {
    source = isObject(source) ? source : {}
    for (const key in source) {
      const value = source[key]

      if (isObject(value) && isObject(target[key]) && !(Array.isArray(value) || Array.isArray(target[key]))) {
        merge(target[key], value)
      } else {
        target[key] = value
      }
    }
    return target
  }

  return sources.reduce((acc, curr) => merge(acc, curr), target)
}

/**
 * 根据日期范围数组生成开始日期和结束日期的key
 * @param {*} dateRange 日期范围数组
 * @param {*} startDateKey 开始日期key
 * @param {*} endDateKey 结束日期key
 * @returns 开始日期key和结束日期key组成的对象
 */
export function generateDateRange(dateRange, startDateKey, endDateKey, config = {}) {
  let options = {
    [startDateKey]: notNullArray(dateRange) && notNull(dateRange[0]) ? dateRange[0] : "",
    [endDateKey]: notNullArray(dateRange) && notNull(dateRange[1]) ? dateRange[1] : ""
  }

  if (config.genHms) {
    if (notNull(options[startDateKey])) {
      options[startDateKey] = moment(options[startDateKey]).format("YYYY-MM-DD HH:mm:ss")
    }
    if (notNull(options[endDateKey])) {
      options[endDateKey] = `${moment(options[endDateKey]).format("YYYY-MM-DD")} 23:59:59`
    }
  }

  return options
}

export function mergeOptionsDate(options, dateRangeKey, startDateKey, endDateKey, config = {}) {
  let newOptions = {
    ...options,
    ...generateDateRange(options[dateRangeKey], startDateKey, endDateKey, config)
  }
  delete newOptions[dateRangeKey]
  return newOptions
}

/**
 * 延迟运行
 * @param {*} callback 回调
 * @param {*} time 时间
 */
export function runOnceLazy(callback, time) {
  let timer = setTimeout(() => {
    callback()
    clearTimeout(timer)
  }, time)
}

// 转换list到map
export function convertListToMap(list, key = "value", toKey = "") {
  let obj = {}
  for (let item of list) {
    obj[item[key]] = isNull(toKey)
      ? {
        ...item
      }
      : item[toKey]
  }
  return obj
}

export function findItemByKey(list, value, key = "key") {
  for (let item of list) {
    if (item[key] === value) {
      return item
    }
  }
}

export function formValidate(formRef) {
  if (isNull(formRef) || isNull(formRef?.validate)) return false
  return new Promise(resolve => {
    formRef.validate(valid => {
      resolve(valid)
    })
  })
}

/**
 * 设置元素不出现滚动条 后续恢复
 * @param {*} query 查询dom信息 css id
 * @param {*} wait 等待时间 毫秒
 * @returns
 */
export function setElementOverflow(query, wait = 100) {
  const element = document.querySelector(query)
  element.style.overflow = "hidden"
  setTimeout(() => {
    element.style.overflow = "auto"
  }, wait)
}

/**
 * 下载文件
 * @param {*} url
 * @param {*} fileName
 */
export function downloadFile(url, fileName) {
  let link = document.createElement("a")
  link.href = url
  link.download = fileName
  link.target = "_blank" // 可选，如果希望在新窗口中下载文件，请取消注释此行
  link.click()
  link = null
}

/**
 * 根据对象的 key 转换为数组
 * @param {*} obj
 * @returns
 */
export const mapToArray = obj => {
  return Object.keys(obj).map(key => {
    if (key === "null") {
      key = null
    }
    return {
      label: obj[key],
      value: key
    }
  })
}
/**
 * 数组转换对象
 * @param {*} arr
 * @returns obj
 */
export const arrayToMap = (arr = [], config = { value: "value", label: "label" }) => {
  let obj = {}
  arr.forEach(item => {
    obj[item[config.value]] = item[config.label]
  })
  return obj
}
/**
 * 数组排除某些值
 * @param {*} arr
 * @returns obj
 */
export const arrayExcludeArray = (value, excludeValue, config = { value: "value", label: "label" }) => {
  if (!Array.isArray(value) || !Array.isArray(excludeValue)) {
    throw new Error("参数类型错误，应该为数组")
  }
  return value.filter(item => !excludeValue.includes(item[config.value]))
}
/**
 * 根绝枚举值获取中文
 */
export const renderMapText = (map = {}, val, defaultVal = "-") => {
  if (notNull(val) && notNull(map[val])) return map[val]
  return defaultVal
}

/**
 * 计时任务
 */
export class TimeTask {
  // 计数器
  count = 0
  // 延时计时器id
  timerId = null
  // 轮询计时器id
  intervalId = null
  // 是否自动增长计数
  isAutoIncrement = true

  constructor(opts = {}) {
    this.isAutoIncrement = opts.isAutoIncrement || true
  }

  /**
   * 每N秒执行计时器
   * @param {Object} call 处理函数
   * @param {Object} time 时间
   */
  run(call, time) {
    this.close()
    this.intervalId = setInterval(() => {
      call()
      if (this.isAutoIncrement) this.count++
      // this.run(call, time);
    }, time)
  }

  /**
   * N秒后执行计时器
   * @param {Object} call 处理函数
   * @param {Object} time 时间
   */
  runOnce(call, time) {
    this.close()
    this.timerId = setTimeout(() => {
      call()
    }, time)
  }

  /**
   * 关闭计时器
   */
  close() {
    if (this.timerId) {
      console.log("close timerId")
      clearTimeout(this.timerId)
      this.timerId = null
    }
    if (this.intervalId) {
      console.log("close intervalId")
      clearInterval(this.intervalId)
      this.intervalId = null
    }
    this.count = 0
  }
}

// 处理大于万的数字
export function formartMoney(num, toFixedNum = 2) {
  // if (Number(num) < 9999) return num
  // let countNum = num
  let unit = ""
  let val = num
  let money = num
  //转换数字
  if (num > 9999) {
    money = (num / 10000).toFixed(toFixedNum)
    val = money + "万"
    unit = "万"
  }
  if (num > 99999999) {
    money = (num / 100000000).toFixed(toFixedNum)
    val = money + "亿"
    unit = "亿"
  }
  return {
    val,
    unit,
    money
  }
}

// 处理大于万的数字
export function toMoney(num, toFixedNum = 2) {
  const { val } = formartMoney(num, toFixedNum)
  return val
}

/**
 * 把多行文本框的字符串转为字符串数组
 * @param {*} str 字符串
 */
export function convertStrLineList(str = "") {
  let inList = str ? str.split("\n") : []
  inList = inList.map(item => item.replace(/ /g, ""))
  let successList = []
  for (let item of inList) {
    if (isNull(item)) continue

    successList.push(item)
  }

  return successList
}

export function isNullArray(v) {
  return !v || !Array.isArray(v) || !v.length
}

/**
 * 自顶而下深度遍历树
 */
export function forEachTree(list, parent, callback) {
  if (isNull(list)) return
  if (Array.isArray(list)) {
    for (const item of list) {
      if (item.children) {
        callback && callback(item, parent)
        forEachTree(item.children, item, callback)
      } else {
        callback && callback(item, parent)
      }
    }
  }
}

/**
 * 自顶而下深度遍历树
 * @param {Array} list 树形列表
 * @param {Object} parent 父节点
 * @param {Number} level 层级
 * @param {Function} callback 回调函数
 */
export function forEachTreeLevel(list = [], parent = null, level = 0, callback) {
  if (isNull(list) || !list.length) return
  if (Array.isArray(list)) {
    for (const item of list) {
      if (item.children) {
        callback && callback(item, parent, level)
        forEachTreeLevel(item.children, item, level + 1, callback)
      } else {
        callback && callback(item, parent, level)
      }
    }
  }
}

/**
 * 排除掉值为空的属性
 * @param {*} object 对象
 */
export function excludeKeysByEmptyVal(object) {
  let obj = {}
  for (let key in object) {
    if (Object.prototype.hasOwnProperty.call(object, key)) {
      if (notNull(object[key])) obj[key] = object[key]
    }
  }
  return obj
}

/**
 * 根据一个对象的数据结构，将原对象的属性值剔除出来，返回一个新对象
 * @param {*} targetObj 目标对象
 * @param {*} sourceObj 原对象
 * @param {*} config 配置
 * @param {*} config.convertFn 属性赋值时的处理函数
 * @returns 新对象
 */
export function copyObjValByObjKeys(targetObj, sourceObj, config) {
  targetObj = targetObj || {}
  sourceObj = sourceObj || {}
  config = config || { convertFn: v => v }
  const keys = Object.keys(targetObj)
  const newObj = {}
  for (const key of keys) {
    newObj[key] = config.convertFn ? config.convertFn(sourceObj[key]) : sourceObj[key]
  }
  return newObj
}

/**
 * 根据key数组排除不需要的属性
 */
export function excludeObjKeys(obj, keys = []) {
  const newObj = {}
  for (const key in obj) {
    if (keys.includes(key)) continue
    newObj[key] = obj[key]
  }
  return newObj
}
/**
 * @description 去除字符串中的空格
 * @param {String} str 字符串
 * */
export function strRemoveSpace(str) {
  return typeof str === "string" ? str.replace(/\s/g, "") : str
}
