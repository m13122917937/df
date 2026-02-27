// o2o国补
export const isGovSubsidyO2O = order => {
  return Boolean(Array.isArray(order?.orderTag) && order.orderTag.includes("16"))
}

// 自营国补
export const isGovSubsidySelf = order => {
  return Boolean(Array.isArray(order?.orderTag) && order.orderTag.includes("15"))
}

export const isGovSubsidySelfDelivery = order => {
  return isGovSubsidySelf(order) && !!order.thirdCFlag
}

// 是否国补订单
export const isGovSubsidy = order => {
  return isGovSubsidyO2O(order) || isGovSubsidySelf(order)
}
export const isGovSubsidyDelivery = order => {
  return isGovSubsidy(order) && !!order.thirdCFlag
}

export const hasImeiConfig = config => {
  return ![config.needImei, config.needImei1, config.needImei2].every(val => [undefined, null].includes(val))
}

// 是否微派订单
export const isWeiPai = order => {
  return !isGovSubsidy(order) && Boolean(Array.isArray(order?.orderTag) && order.orderTag.includes("17"))
}
