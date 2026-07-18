import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import { aesDecrypt } from '@/utils/aes'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['Authorization'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    let res = response.data
    console.log('原始响应数据:', res)
    console.log('响应数据类型:', typeof res)
    // AES解密处理（使用ECB模式，无需IV）
    // 如果响应数据是加密的字符串，需要先解密
    const AES_KEY = process.env.VUE_APP_AES_KEY || '' // 从环境变量获取密钥
    console.log('AES_KEY:', AES_KEY, '长度:', AES_KEY.length)

    if (AES_KEY) {
      // 如果响应数据是字符串，尝试解密整个响应
      if (typeof res === 'string') {
        console.log('响应是字符串，尝试解密...')
        try {
          const decrypted = aesDecrypt(res, AES_KEY)
          console.log('解密结果:', decrypted)
          res = decrypted
          // 如果解密后的数据是字符串，尝试解析为JSON
          if (typeof res === 'string') {
            try {
              res = JSON.parse(res)
              console.log('解析JSON成功')
            } catch (e) {
              console.log('不是JSON格式，保持原样')
            }
          }
        } catch (error) {
          console.warn('AES解密失败，使用原始数据:', error)
        }
      } else if (res && typeof res === 'object') {
        console.log('响应是对象，检查是否需要解密...')
        // 检查是否有加密字段（可能是 data, result, content 等）
        const encryptedFields = ['data', 'result', 'content', 'body']
        for (const field of encryptedFields) {
          if (res[field] && typeof res[field] === 'string') {
            console.log(`发现 ${field} 字段是字符串，尝试解密...`)
            try {
              const decrypted = aesDecrypt(res[field], AES_KEY)
              console.log(`${field} 解密结果:`, decrypted)
              res[field] = decrypted
              // 如果解密后的data是字符串，尝试解析为JSON
              if (typeof res[field] === 'string') {
                try {
                  res[field] = JSON.parse(res[field])
                  console.log(`${field} 解析JSON成功`)
                } catch (e) {
                  console.log(`${field} 不是JSON格式，保持原样`)
                }
              }
            } catch (error) {
              console.warn(`AES解密${field}字段失败:`, error)
            }
          }
        }
      }
    } else {
      console.warn('未配置AES_KEY，跳过解密')
    }

    const code = res.code || 200
    if (code !== 200) {
      Message({
        message: res.msg || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      if (res.code === 403 || res.code === 401) {
        store.dispatch('user/resetToken').then(() => {
          location.reload()
        })
        return
      } else {
        return Promise.reject(res)
      }
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
