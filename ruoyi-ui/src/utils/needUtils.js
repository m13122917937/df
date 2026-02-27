import { Message } from "element-ui"

/**
 * 验证数据方法
 * @param {*} data 需要验证的对象：{a:1}
 * @param {*} ruleInfo 规则数据：{a:[{rule:"must",error:"必传参数"}]}
 * @returns
 */
export function valiData(data, ruleInfo) {
  for (let key in ruleInfo) {
    for (let item of ruleInfo[key]) {
      if (item.rule === "must" && item.prop) {
        if (data[item.prop] == item.value && !data[key]) {
          errMsg(item.error)
          return false
        } else if (data[item.prop] != item.value) {
          break
        }
      } else if (item.rule === "must") {
        if (!data[key]) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "mustNumber") {
        const text = data[key] + ""
        if (!text || data[key] === undefined || data[key] === null) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "arr") {
        if (!(Array.isArray(data[key]) && data[key].length)) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "lt" && item.type === "now") {
        if (new Date() > new Date(data[key])) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "lt" && item.number !== undefined) {
        if (data[key] <= item.number) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "gt" && item.number !== undefined) {
        if (data[key] >= item.number) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "int") {
        let intTemp = parseFloat(data[key])
        if (isNaN(intTemp) || parseInt(data[key]) != intTemp) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "number") {
        if (!/^[0-9]+$/.test(data[key])) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "float") {
        let floatTemp = parseFloat(data[key])
        if (isNaN(floatTemp) || data[key] != floatTemp) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "gt" && item.prop) {
        if (parseFloat(data[key]) >= parseFloat(data[item.prop])) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "phone") {
        if (!/^(0\d{2,3}-\d{5,9}|1[3-9]\d{9}|1[3-9]\d{9}-\d{4,6})$/.test(data[key])) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "code") {
        if (!/^([0-9A-Za-z]+)$/.test(data[key])) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "length") {
        let dataLength = data[key] ? data[key].length : 0
        if ((item.lt && dataLength < item.lt) || (item.gt && dataLength > item.gt)) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "float2") {
        let tempArr = data[key].toString().split(".")
        if (tempArr.length > 1 && tempArr[1].length > 2) {
          errMsg(item.error)
          return false
        }
      } else if (item.rule === "eq") {
        if (data[key] == item.value) {
          errMsg(item.error)
          return false
        }
      } else if (item.reg) {
        if (!item.reg.test(data[key])) {
          errMsg(item.error)
          return false
        }
      }
    }
  }
  return true
}

function errMsg(text) {
  Message({ message: text, dangerouslyUseHTMLString: true, type: "error" })
}

// function errSuccess(text) {
//   Message({ message: text, dangerouslyUseHTMLString: true, type: "success" })
// }

// 太阳码下载
export const download = (url, fileName) => {
  let bas64 = url
  let byteCharacters = atob(bas64.replace(/^data:image\/(png|jpeg|jpg);base64,/, ""))
  let byteNumbers = new Array(byteCharacters.length)
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i)
  }
  let byteArray = new Uint8Array(byteNumbers)
  let blob = new Blob([byteArray], {
    type: undefined
  })
  let aLink = document.createElement("a")
  aLink.download = `${fileName}.jpg`
  aLink.href = URL.createObjectURL(blob)
  aLink.click()
}
