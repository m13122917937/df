import * as XLSX from "xlsx"
import { resetMsg } from "@/utils/resetMessage"

/**
 * 读取excel内容（新增字段缺失校验）
 * @param {*} file - Excel文件对象
 * @param {Object} keyMap - 字段映射配置 {中文key: 英文key}
 * @returns {Promise<Array>} 转换后的数据列表（或拒绝Promise并提示错误）
 */
export const readExcel = (file, keyMap) => {
  return new Promise((resolve, reject) => {
    try {
      const fileReader = new FileReader()
      fileReader.onload = event => {
        const arrayBuffer = event.target.result
        const workbook = XLSX.read(arrayBuffer, { type: "array" })
        const sheetName = workbook.SheetNames[0]
        const rawDataList = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName])

        // 转换并校验字段完整性（新增校验逻辑）
        const transformedList = transformList(rawDataList, keyMap)
        resolve(transformedList)
      }
      fileReader.onerror = error => {
        resetMsg.error("读取文件失败")
        reject(error)
      }
      fileReader.readAsArrayBuffer(file)
    } catch (e) {
      // 捕获字段缺失等校验错误
      resetMsg.error(`解析Excel文件失败`)
      reject(e)
    }
  })
}

/**
 * 转换列表中每个对象的中文key为自定义英文key，并校验字段完整性
 * @param {Array} list - 原始对象列表
 * @param {Object} keyMap - 字段映射配置 {中文key: 英文key}
 * @returns {Array} 转换后的有效对象列表
 * @throws {Error} 字段缺失时抛出错误（包含缺失字段信息）
 */
export const transformList = (list, keyMap) => {
  if (!Array.isArray(list)) return []
  if (Object.keys(keyMap).length === 0) throw new Error("keyMap不能为空")

  // 收集所有缺失的字段（全局检查）
  const missingFields = new Set()

  // 转换并校验每个对象
  const transformed = list
    .map(item => {
      // 过滤无效对象（null/非对象/数组）
      if (!item || typeof item !== "object" || Array.isArray(item)) return {}

      // 校验当前对象是否包含所有keyMap字段
      const itemKeys = Object.keys(item)
      Object.keys(keyMap).forEach(chineseKey => {
        if (!itemKeys.includes(chineseKey)) {
          missingFields.add(chineseKey) // 记录缺失字段
        }
      })

      // 转换有效字段（仅保留有映射的key）
      return Object.keys(item).reduce((acc, chineseKey) => {
        const englishKey = keyMap[chineseKey]
        if (englishKey) acc[englishKey] = item[chineseKey]
        return acc
      }, {})
    })
    .filter(obj => Object.keys(obj).length > 0) // 过滤空对象

  // 若有字段缺失，抛出明确错误
  if (missingFields.size > 0) {
    resetMsg.error(`解析Excel文件失败`)
    throw new Error(`解析Excel文件失败`)
  }
  if (transformed?.length === 0) {
    resetMsg.error(`没有填写数据`)
    throw new Error(`没有填写数据`)
  }

  return transformed
}
