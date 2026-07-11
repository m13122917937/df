/**
 * 订单类型（采购类型）枚举
 * 对应后端 OrderConsts.OrderType：入仓(1)、代发(2)、销售订单(3)
 */
export const ORDER_TYPE = {
  PROCUREMENT: { code: 1, label: '入仓' },
  O2O: { code: 2, label: '代发' },
  SALES: { code: 3, label: '销售订单' },
}

export const ORDER_TYPE_OPTIONS = [
  { label: '全部类型', value: '' },
  { label: '入仓', value: 1 },
  { label: '代发', value: 2 },
  { label: '销售订单', value: 3 },
]

export function getOrderTypeLabel(code) {
  const map = {
    1: '入仓',
    2: '代发',
    3: '销售订单',
  }
  return map[code] || '代发'
}
