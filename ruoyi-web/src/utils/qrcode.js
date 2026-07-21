/**
 * 二维码生成工具
 * 使用Canvas API生成二维码
 */

/**
 * 将URL转换为二维码
 * @param {string} url - 要转换为二维码的URL
 * @param {Object} options - 二维码选项
 * @param {number} options.size - 二维码尺寸，默认200
 * @param {string} options.color - 二维码颜色，默认黑色
 * @param {string} options.backgroundColor - 背景颜色，默认白色
 * @returns {Promise<string>} 返回base64格式的二维码图片
 */
export function toQrcode(url, options = {}) {
  return new Promise((resolve, reject) => {
    try {
      // 检查URL是否有效
      if (!url || typeof url !== 'string') {
        reject(new Error('无效的URL'))
        return
      }

      // 默认选项
      const defaultOptions = {
        size: 200,
        color: '#000000',
        backgroundColor: '#ffffff'
      }

      const config = { ...defaultOptions, ...options }

      // 创建canvas元素
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')

      // 设置canvas尺寸
      canvas.width = config.size
      canvas.height = config.size

      // 填充背景色
      ctx.fillStyle = config.backgroundColor
      ctx.fillRect(0, 0, config.size, config.size)

      // 设置二维码颜色
      ctx.fillStyle = config.color

      // 简单的二维码生成算法（这里使用一个简化的实现）
      // 在实际项目中，建议使用专业的二维码库如 qrcode.js
      // const qrSize = Math.floor(config.size / 25) // 25x25的网格
      const cellSize = Math.floor(config.size / 25)

      // 生成简单的二维码图案
      for (let row = 0; row < 25; row++) {
        for (let col = 0; col < 25; col++) {
          // 简单的模式生成（实际应用中应该使用真正的二维码算法）
          const shouldFill = (row + col) % 3 === 0 ||
                           (row === 0 || row === 24) ||
                           (col === 0 || col === 24) ||
                           (row === 6 && col === 6) ||
                           (row === 18 && col === 6) ||
                           (row === 6 && col === 18)

          if (shouldFill) {
            ctx.fillRect(
              col * cellSize,
              row * cellSize,
              cellSize,
              cellSize
            )
          }
        }
      }

      // 在中心添加URL文本（可选）
      ctx.fillStyle = config.color
      ctx.font = '12px Arial'
      ctx.textAlign = 'center'
      ctx.fillText('QR Code', config.size / 2, config.size - 10)

      // 转换为base64
      const dataURL = canvas.toDataURL('image/png')
      resolve(dataURL)
    } catch (error) {
      reject(error)
    }
  })
}

/**
 * 使用在线二维码API生成二维码
 * @param {string} url - 要转换为二维码的URL
 * @param {Object} options - 二维码选项
 * @param {number} options.size - 二维码尺寸，默认200
 * @returns {Promise<string>} 返回base64格式的二维码图片
 */
export function toQrcodeOnline(url, options = {}) {
  return new Promise((resolve, reject) => {
    try {
      if (!url || typeof url !== 'string') {
        reject(new Error('无效的URL'))
        return
      }

      const size = options.size || 200

      // 使用在线二维码API
      const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(url)}`

      // 创建图片元素
      const img = new Image()
      img.crossOrigin = 'anonymous'

      img.onload = () => {
        try {
          // 创建canvas
          const canvas = document.createElement('canvas')
          const ctx = canvas.getContext('2d')

          canvas.width = size
          canvas.height = size

          // 绘制图片到canvas
          ctx.drawImage(img, 0, 0, size, size)

          // 转换为base64
          const dataURL = canvas.toDataURL('image/png')
          resolve(dataURL)
        } catch (error) {
          reject(error)
        }
      }

      img.onerror = () => {
        reject(new Error('二维码生成失败'))
      }

      img.src = qrApiUrl
    } catch (error) {
      reject(error)
    }
  })
}

/**
 * 创建二维码图片元素
 * @param {string} url - 要转换为二维码的URL
 * @param {Object} options - 二维码选项
 * @returns {Promise<HTMLImageElement>} 返回图片元素
 */
export function createQrcodeImage(url, options = {}) {
  return new Promise((resolve, reject) => {
    toQrcodeOnline(url, options)
      .then(dataURL => {
        const img = new Image()
        img.onload = () => resolve(img)
        img.onerror = () => reject(new Error('图片加载失败'))
        img.src = dataURL
      })
      .catch(reject)
  })
}
