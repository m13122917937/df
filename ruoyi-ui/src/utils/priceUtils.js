/**
 * 价格相关工具函数
 */

/**
 * 格式化数字
 * @param {*} val 要格式化的值
 * @param {Object} options 格式化选项
 * @returns {string} 格式化后的字符串
 */
export function formatNumber(val, options = {}) {
  const {
    maximumFractionDigits = 0,
    minimumFractionDigits = 0,
    locale = 'zh-CN'
  } = options
  
  const n = Number(val)
  if (!isFinite(n)) return '-'
  
  return n.toLocaleString(locale, { 
    minimumFractionDigits, 
    maximumFractionDigits 
  })
}

/**
 * 格式化价格（保留2位小数）
 * @param {*} val 要格式化的值
 * @returns {string} 格式化后的价格字符串
 */
export function formatPrice(val) {
  return formatNumber(val, { 
    minimumFractionDigits: 2, 
    maximumFractionDigits: 2 
  })
}

/**
 * 生成价格选项数据
 * @param {Object} row 数据行对象
 * @param {Object} config 配置选项
 * @returns {Array} 处理后的价格选项数组
 */
export function generatePriceOptions(row, config = {}) {
  const {
    tradePriceField = 'tradePrice',
    priceFields = [
      { key: 'priceHighest', value: 'priceHighest' },
      { key: 'priceHign', value: 'priceHign' },
      { key: 'priceLow', value: 'priceLow' },
      { key: 'priceLowest', value: 'priceLowest' }
    ],
    highlightTradePrice = true,
    showStriked = true
  } = config

  const mapping = priceFields.map(field => ({
    key: field.key,
    value: row[field.value]
  }))
  
  const tradePrice = Number(row[tradePriceField])
  const idx = mapping.findIndex(m => Number(m.value) === tradePrice)
  
  return mapping.map((m, i) => ({
    ...m,
    highlight: highlightTradePrice && i === idx,
    striked: showStriked && idx !== -1 && i < idx
  }))
}

/**
 * 获取价格显示配置
 * @param {string} type 价格类型
 * @returns {Object} 配置对象
 */
export function getPriceConfig(type = 'default') {
  const configs = {
    // 默认配置
    default: {
      priceFields: [
        { key: 'priceHighest', value: 'priceHighest' },
        { key: 'priceHign', value: 'priceHign' },
        { key: 'priceLow', value: 'priceLow' },
        { key: 'priceLowest', value: 'priceLowest' }
      ],
      tradePriceField: 'tradePrice',
      currencySymbol: '￥',
      highlightTradePrice: true,
      showStriked: true
    },
    // 简化配置（只显示最高和最低价）
    simple: {
      priceFields: [
        { key: 'priceHighest', value: 'priceHighest' },
        { key: 'priceLowest', value: 'priceLowest' }
      ],
      tradePriceField: 'tradePrice',
      currencySymbol: '￥',
      highlightTradePrice: true,
      showStriked: true
    },
    // 自定义价格字段
    custom: (fields) => ({
      priceFields: fields,
      tradePriceField: 'tradePrice',
      currencySymbol: '￥',
      highlightTradePrice: true,
      showStriked: true
    })
  }
  
  return configs[type] || configs.default
}
