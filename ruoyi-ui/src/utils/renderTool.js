import { OrderStatusMap } from "@/enum/merchant/order"
import { notNull, isNull } from "."

// 渲染商品名称
export function renderProducModel(item) {
  if (isNull(item)) return "-"
  return notNull(item.productName) || notNull(item.specName) ? `${item.productName || ""} ${item.specName || ""}` : "-"
}

export function notNullProductModel(item) {
  return notNull(item) && notNull(item.productName) && notNull(item.specName)
}

export function renderMoney(row, key, defaultVal = "-") {
  if (notNull(row[key])) return `￥${row[key]}`
  return defaultVal
}

export function renderDurationTime(row) {
  let durationTime = row?.durationTime
  if (isNull(row) || isNull(row.durationTime)) {
    return "-"
  }
  if (durationTime === "0小时") return "不足1小时"
  return durationTime
}

/**
 * 渲染字典数据
 * @param {*} map 字典
 * @param {*} val 值
 * @param {*} defaultVal 默认值
 * @returns
 */
export function renderMapData(map, val, defaultVal = "-") {
  if (notNull(val) && notNull(map[val])) return map[val].label
  return defaultVal
}

/**
 * 购机通订单状态文案
 * @param {*} row 订单记录
 * @returns
 */
export function renderOrderStatusText(row) {
  if (notNull(row?.refundStatus) && row?.refundStatus === 2) return "已退货退款"
  return renderMapData(OrderStatusMap, row?.orderStatus, "")
}
