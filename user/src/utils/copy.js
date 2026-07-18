import { Message } from 'element-ui'

/**
 * 复制文本到剪贴板
 * @param {string} text - 要复制的文本
 * @param {Object} vueInstance - Vue 实例，用于显示消息提示
 */
export function copyText(text, vueInstance = null) {
  if (!text) return

  // 使用现代浏览器的 Clipboard API
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard
      .writeText(text)
      .then(() => {
        Message.success('复制成功')
      })
      .catch(() => {
        Message.error('复制失败')
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
        Message.success('复制成功')
      } else {
        throw new Error('复制失败')
      }
    } catch (err) {
      Message.error('复制失败')
    } finally {
      document.body.removeChild(textArea)
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

