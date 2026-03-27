import { Message } from 'element-ui'

/**
 * 格式化日期时间
 * @param {string|number|Date} dateTime - 日期时间字符串、时间戳或Date对象
 * @returns {string} 格式化后的日期时间
 */
export function formatDateTime(dateTime) {
  if (!dateTime) return '-'
  try {
    let date
    if (typeof dateTime === 'number') {
      // 时间戳
      date = new Date(dateTime)
    } else if (dateTime instanceof Date) {
      // Date 对象
      date = dateTime
    } else {
      // 字符串处理，兼容 yyyy-MM-dd HH:mm:ss 格式
      if (dateTime.includes(' ')) {
        date = new Date(dateTime.replace(' ', 'T'))
      } else {
        date = new Date(dateTime)
      }
    }
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return String(dateTime)
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.warn('formatDateTime error:', dateTime, error)
    return String(dateTime)
  }
}

/**
 * 复制文本到剪贴板
 * @param {string} text - 要复制的文本
 * @param {Object} vueInstance - Vue 实例，用于显示消息提示
 */
export function copyText(text, vueInstance = null) {
  if (!text) return
  
  // 使用现代浏览器的 Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard.writeText(text).then(() => {
      Message.success("复制成功");
      // 移除焦点以避免可访问性警告
      if (document.activeElement && document.activeElement.blur) {
        document.activeElement.blur();
      }
    }).catch(() => {
      Message.error("复制失败");
    })
  } else {
    // 降级方案：使用传统的 document.execCommand
    const textArea = document.createElement('textarea')
    textArea.value = text
    textArea.style.position = 'fixed'
    textArea.style.left = '-999999px'
    textArea.style.top = '-999999px'
    document.body.appendChild(textArea)
    textArea.focus()
    textArea.select()
    
    try {
      const result = document.execCommand('copy')
      if (result) {
        Message.success("复制成功");
      } else {
        throw new Error('复制失败')
      }
    } catch (err) {
      Message.error("复制失败");
    } finally {
      document.body.removeChild(textArea)
      // 移除焦点以避免可访问性警告
      if (document.activeElement && document.activeElement.blur) {
        document.activeElement.blur();
      }
    }
  }
}

/**
 * 创建 Vue 组件中使用的 copyText 方法
 * @param {Object} vueInstance - Vue 实例
 * @returns {Function} 绑定到 Vue 实例的 copyText 方法
 */
export function createCopyTextMethod(vueInstance) {
  return function(text) {
    copyText(text, vueInstance)
  }
}

/**
 * 格式化价格，保留两位小数
 * @param {number|string} val - 要格式化的值
 * @returns {string} 格式化后的价格
 */
export function formatPrice(val) {
  const n = Number(val)
  if (!isFinite(n)) return '-'
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

/**
 * 格式化数字，不带小数
 * @param {number|string} val - 要格式化的值
 * @returns {string} 格式化后的数字
 */
export function formatNumber(val) {
  const n = Number(val)
  if (!isFinite(n)) return '-'
  return n.toLocaleString('zh-CN', { maximumFractionDigits: 0 })
}

/**
 * 格式化账期
 * @param {number|string} val - 账期值
 * @returns {string} 格式化后的账期
 */
export function formatAccountingPeriod(val) {
  const n = Number(val)
  if (!isFinite(n) || n < 0) return '-'
  return n === 0 ? '当天' : `T+${n}`
}

/**
 * 创建 Vue 组件中使用的 formatDateTime 方法
 * @returns {Function} formatDateTime 方法
 */
export function createFormatDateTimeMethod() {
  return function(row, column, cellValue) {
    return formatDateTime(cellValue)
  }
}

/**
 * 解析日期字符串，支持多种格式
 * @param {string} dateString - 日期字符串
 * @returns {Date|null} 解析后的日期对象，解析失败返回 null
 */
export function parseDateString(dateString) {
  if (!dateString) return null;
  
  try {
    // 处理 YYYY-MM-DD HH:mm:ss 格式
    if (dateString.includes(' ')) {
      return new Date(dateString.replace(' ', 'T'));
    } else {
      return new Date(dateString);
    }
  } catch (error) {
    console.warn('Error parsing date string:', dateString, error);
    return null;
  }
}

/**
 * 验证日期是否有效
 * @param {Date} date - 日期对象
 * @returns {boolean} 是否有效
 */
export function isValidDate(date) {
  return date instanceof Date && !isNaN(date.getTime());
}

/**
 * 获取时间状态样式类
 * @param {Object} row - 行数据对象
 * @param {string} row.lastShippingTime - 最晚发货时间
 * @param {string} row.shipmentsTime - 实际发货时间（可选）
 * @returns {string} CSS 类名
 */
export function getTimeStatusClass(row) {
  const now = new Date();
  const lastShippingTime = row.lastShippingTime;
  const shipmentsTime = row.shipmentsTime;
  
  if (!lastShippingTime) {
    return '';
  }
  
  // 解析最晚发货时间
  const lastShippingDate = parseDateString(lastShippingTime);
  if (!isValidDate(lastShippingDate)) {
    console.warn('Invalid lastShippingTime:', lastShippingTime);
    return '';
  }
  
  // 如果已发货，检查是否超时
  if (shipmentsTime) {
    const shipmentsDate = parseDateString(shipmentsTime);
    if (isValidDate(shipmentsDate) && now > shipmentsDate) {
      return 'time-overdue';
    }
  }
  
  // 检查最晚发货时间是否已过期
  if (now > lastShippingDate) {
    return 'time-overdue';
  }
  
  // 最晚发货时间 - 当前时间 < 24小时 → 浅红色警告
  const timeDiff = lastShippingDate.getTime() - now.getTime();
  const hoursDiff = timeDiff / (1000 * 60 * 60);
  
  if (hoursDiff < 24 && hoursDiff > 0) {
    return 'time-warning';
  }
  
  return '';
}

/**
 * 创建 Vue 组件中使用的 getTimeStatusClass 方法
 * @returns {Function} getTimeStatusClass 方法
 */
export function createGetTimeStatusClassMethod() {
  return function(row) {
    return getTimeStatusClass(row);
  }
}
