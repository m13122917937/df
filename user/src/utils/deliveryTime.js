/**
 * 发货时效相关工具方法
 */

/**
 * 获取发货时效显示文本
 * @param {number} deliveryDeadline - 发货时效值
 * @returns {string} 显示文本
 */
export function getDeliveryTimeText(deliveryDeadline) {
  const value = deliveryDeadline || 0

  switch (value) {
    case -1:
      return '昨天'
    case 0:
      return '今天'
    case 1:
      return '明天'
    case 2:
      return '后天'
    default:
      // 对于其他数值，显示相对天数
      if (value > 2) {
        return `${value}天后`
      } else if (value < -1) {
        return `${Math.abs(value)}天前`
      }
      return `${value}天`
  }
}
