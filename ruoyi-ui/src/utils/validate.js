import { isNull, valueOrDefault } from "."

/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @param {string} url
 * @returns {Boolean}
 */
export function isHttp(url) {
  return /^https?:\/\//.test(url)
}

/**
 * 判断是否为空
 * @param {any} val
 * @returns {Boolean}
 */
export function isEmpty(val) {
  if (typeof val === "boolean") {
    return false
  }
  if (typeof val === "number") {
    return false
  }
  if (val == null || val === "") {
    return true
  }
  if (typeof val === "string") {
    return val.trim().length === 0
  }
  if (Array.isArray(val)) {
    return val.length === 0
  }
  if (typeof val === "object") {
    return Object.keys(val).length === 0
  }
  return false
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validUsername(str) {
  const valid_map = ["admin", "editor"]
  return valid_map.indexOf(str.trim()) >= 0
}

/**
 * @param {string} url
 * @returns {Boolean}
 */
export function validURL(url) {
  const reg =
    /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

/**
 * @param {string} phone
 * @returns {Boolean}
 * 1开头的11位数字（手机号）
 */
export function validPhone(phone) {
  const reg = /^1[0-9]{10}$/
  return reg.test(phone)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validLowerCase(str) {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validUpperCase(str) {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function validAlphabets(str) {
  const reg = /^[A-Za-z]+$/
  return reg.test(str)
}

/**
 * @param {string} email
 * @returns {Boolean}
 */
// export function validEmail(email) {
//   const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
//   return reg.test(email)
// }

/**
 * @param {string} str
 * @returns {Boolean}
 */
export function isString(str) {
  if (typeof str === "string" || str instanceof String) {
    return true
  }
  return false
}

/**
 * @param {Array} arg
 * @returns {Boolean}
 */
export function isArray(arg) {
  if (typeof Array.isArray === "undefined") {
    return Object.prototype.toString.call(arg) === "[object Array]"
  }
  return Array.isArray(arg)
}

// 数量大于0的校验
export function validatePrice(rule, value, callback) {
  if (value <= 0) callback("需要大于0")
  callback()
}

/**
 * 金额校验
 * 暂时只处理了能输入0、 正数，其它情况后续有时间优化
 * @param {*} e 事件对象
 * @param {*} formObj form对象
 * @param {*} key form内的字段名
 */
export function handleInputMoney(config = {}) {
  let minIntegerLen = valueOrDefault(config.minIntegerLen, 0)
  let maxIntegerLen = valueOrDefault(config.maxIntegerLen, 14)
  let minDecimalLen = valueOrDefault(config.minDecimalLen, 0)
  let maxDecimalLen = valueOrDefault(config.maxDecimalLen, 2)

  return function (e, formObj, key, callback) {
    let decimalReg = maxDecimalLen > 0 ? `(\\.\\d{${minDecimalLen},${maxDecimalLen}})` : ""
    const reg = new RegExp(`^\\d{${minIntegerLen},${maxIntegerLen}}${decimalReg}?$`)

    if (String(e)) {
      if (reg.test(String(e))) {
        formObj[key] = handleCalibrate(e)
      }
    } else {
      formObj[key] = ""
    }

    callback && callback(formObj[key])
  }
}

// 金额处理不能为0的处理
export function handleCalibrate(e) {
  if (String(e).charAt(0) === ".") return `0${e}`
  if (String(e).charAt(0) === "0" && String(e).charAt(1) === "0") {
    return String(e).charAt(0)
  }
  if (String(e).charAt(0) === "0" && String(e).charAt(1) !== ".") {
    return 0
  }

  return e
}

/**
 * 处理输入 111. 这种特殊情况
 * @param {*} formObj form本身的字段名
 * @param {*} key form内的字段名
 * @returns
 */
export function handleBlurMoney(e, formObj, key) {
  try {
    if (isNull(formObj[key])) return
    formObj[key] = Number(formObj[key])
  } catch (error) {
    console.error(error)
  }
}

export const handleInputInteger = handleInputMoney({ maxDecimalLen: 0 })

export const validateFuncObj = {
  handleBlurMoney,
  handleInputMoney: handleInputMoney(),
  handleInputInteger: handleInputMoney({ maxDecimalLen: 0 })
}

/**
 * 合并表单规则对象
 * @param  {...any} rulesList 多个规则对象 rules1,rules2,rules3
 * @returns {Object} 合并后的新规则对象
 */
export function mergeFormRules(...rulesList) {
  let newRules = {}
  for (const ruleObj of rulesList) {
    for (const key of Object.keys(ruleObj)) {
      if (newRules[key]) {
        newRules[key] = [...newRules[key], ...ruleObj[key]]
      } else {
        newRules[key] = ruleObj[key]
      }
    }
  }

  return newRules
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

/**
 * 路径匹配函数
 * @param {string} pattern 匹配模式
 * @param {string} path 要匹配的路径
 * @returns {Boolean}
 */
export function isPathMatch(pattern, path) {
  if (pattern === path) {
    return true
  }
  
  // 支持通配符匹配
  if (pattern.includes('*')) {
    const regex = new RegExp('^' + pattern.replace(/\*/g, '.*') + '$')
    return regex.test(path)
  }
  
  // 支持路径前缀匹配
  if (pattern.endsWith('/')) {
    return path.startsWith(pattern)
  }
  
  return false
}
