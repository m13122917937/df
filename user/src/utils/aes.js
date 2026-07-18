import CryptoJS from 'crypto-js'

/**
 * AES解密
 * @param {string} encryptedData - 加密的数据
 * @param {string} key - 密钥（Base64编码）
 * @param {string} iv - 初始向量（可选）
 * @returns {string} 解密后的数据
 */

/**
 * AES解密（兼容Hutool SecureUtil.aes加密方式）
 * 服务端使用: SecureUtil.aes(Base64.decode(key)).encryptBase64(json)
 * 所以需要：
 * 1. 密钥先Base64解码
 * 2. 密文先Base64解码
 * 3. 使用ECB模式
 */
export function aesDecrypt(encryptedData, key, iv = null) {
  try {
    console.log('=== 开始解密（Hutool兼容模式）===')
    console.log('加密数据:', encryptedData)
    console.log('密钥（Base64）:', key)

    // 如果数据不是字符串，先转换为字符串
    const encrypted = typeof encryptedData === 'string' ? encryptedData : JSON.stringify(encryptedData)

    // 步骤1: Base64解码密钥（Hutool使用Base64编码的密钥）
    let keyWordArray
    try {
      keyWordArray = CryptoJS.enc.Base64.parse(key)
      console.log('密钥Base64解码成功，密钥字节数:', keyWordArray.sigBytes)
    } catch (e) {
      console.error('密钥Base64解码失败:', e.message)
      // 如果Base64解码失败，尝试直接使用UTF8解析
      console.log('尝试使用UTF8解析密钥...')
      keyWordArray = CryptoJS.enc.Utf8.parse(key)
    }

    // 步骤2: Base64解码密文（Hutool使用encryptBase64，所以密文是Base64编码的）
    let ciphertext
    try {
      ciphertext = CryptoJS.enc.Base64.parse(encrypted)
      console.log('密文Base64解码成功')
    } catch (e) {
      console.error('密文Base64解码失败:', e.message)
      // 如果Base64解码失败，尝试直接使用（CryptoJS可能会自动处理）
      console.log('尝试直接使用密文...')
      ciphertext = encrypted
    }

    // 步骤3: 使用ECB模式解密（Hutool默认使用ECB模式）
    let decrypted
    if (iv) {
      // 如果提供了IV，使用CBC模式
      const ivWordArray = CryptoJS.enc.Utf8.parse(iv)
      decrypted = CryptoJS.AES.decrypt(
        typeof ciphertext === 'string' ? ciphertext : { ciphertext: ciphertext },
        keyWordArray,
        {
          iv: ivWordArray,
          mode: CryptoJS.mode.CBC,
          padding: CryptoJS.pad.Pkcs7
        }
      )
    } else {
      // 使用ECB模式（Hutool默认）
      decrypted = CryptoJS.AES.decrypt(
        typeof ciphertext === 'string' ? ciphertext : { ciphertext: ciphertext },
        keyWordArray,
        {
          mode: CryptoJS.mode.ECB,
          padding: CryptoJS.pad.Pkcs7
        }
      )
    }

    // 步骤4: 将解密结果转换为UTF8字符串
    const decryptedStr = decrypted.toString(CryptoJS.enc.Utf8)
    console.log('解密后的字符串:', decryptedStr)
    console.log('解密后字符串长度:', decryptedStr.length)

    // 如果解密结果为空，说明解密失败
    if (!decryptedStr || decryptedStr.length === 0) {
      console.error('解密结果为空，可能的原因：')
      console.error('1. 密钥Base64编码不正确')
      console.error('2. 密文Base64编码不正确')
      console.error('3. 密钥或密文格式不匹配')
      return encryptedData
    }

    // 步骤5: 尝试解析为JSON（Hutool加密的是JSON字符串）
    try {
      const parsed = JSON.parse(decryptedStr)
      console.log('成功解析为JSON:', parsed)
      console.log('=== 解密成功 ===')
      return parsed
    } catch (e) {
      console.log('不是JSON格式，返回原始字符串')
      console.log('=== 解密完成 ===')
      return decryptedStr
    }
  } catch (error) {
    console.error('AES解密异常:', error)
    console.error('错误详情:', error.message)
    if (error.stack) {
      console.error('错误堆栈:', error.stack)
    }
    return encryptedData // 解密失败时返回原始数据
  }
}

/**
 * AES加密
 * @param {string|object} data - 要加密的数据
 * @param {string} key - 密钥
 * @param {string} iv - 初始向量（可选）
 * @returns {string} 加密后的数据
 */
export function aesEncrypt(data, key, iv = null) {
  try {
    // 如果数据是对象，先转换为JSON字符串
    const dataStr = typeof data === 'string' ? data : JSON.stringify(data)

    // 将密钥转换为WordArray
    const keyWordArray = CryptoJS.enc.Utf8.parse(key)

    let encrypted

    if (iv) {
      // 使用CBC模式（需要IV）
      const ivWordArray = CryptoJS.enc.Utf8.parse(iv)
      encrypted = CryptoJS.AES.encrypt(dataStr, keyWordArray, {
        iv: ivWordArray,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      })
    } else {
      // 使用ECB模式（不需要IV）
      encrypted = CryptoJS.AES.encrypt(dataStr, keyWordArray, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      })
    }

    return encrypted.toString()
  } catch (error) {
    console.error('AES加密失败:', error)
    return data
  }
}

