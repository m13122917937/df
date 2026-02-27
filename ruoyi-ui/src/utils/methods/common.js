// 采购类型枚举
const EMPurchaseType = {
  WHOLESALE: { value: 0, label: "批发" },
  RETAIL: { value: 1, label: "零售" },
  DROPSHIPPING: { value: 2, label: "一件代发" }
}

let provinceConfig = {
  special: [
    { label: "黑龙江", len: 3 },
    { label: "内蒙古", len: 3 }
  ],
  len: 2
}

// 截取省
export function getProvinceValue(value, config = provinceConfig) {
  if (!value) return ""
  if (value === "中国") return "醒市无仓"
  let newVal = value.slice(0, config.len)
  config.special.forEach((item, i) => {
    if (value.includes(item.label)) newVal = value.slice(0, config.special[i].len)
  })
  return newVal
}

/**
 * @description 获取采购类型
 * @param { String | number } val - 传入编号或文本
 * @param { boolean } [type] - 默认 false 获取采购类型编号 true 获取采购类型名称
 */
export function getPurchaseType(val, type = false) {
  const obj = Object.values(EMPurchaseType).find(item => [item.value, item.label].includes(val))
  if (!obj) return val
  return type ? obj.label : obj.value
}