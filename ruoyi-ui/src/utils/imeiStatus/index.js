import { EActiveStatus, EMActiveStatus } from "@/enum/imei"

let cacheStatus = {}
const className = {
  right: "el-icon-circle-check",
  wrong: "el-icon-circle-close",
  wait: "el-icon-time"
}

// 未输入串码
const noUploaded = { label: "-", status: "" }
// 验证通过
const verifyPassed = { label: "验证通过", status: "right" }

const sellStatus = { noVerify: 0, noSold: 1, alreadySold: 2 }
const sellStatusObj = {
  0: { label: "验证中", status: "wait" },
  1: verifyPassed,
  2: { label: "已销售", status: "wrong" }
}

// 串码是否全部上传
function imeiUploaded(row) {
  return row.imeiNumber && row.quantity === row.imeiNumber.enteringNumber
}
function getStatusObj(row) {
  // 非发货前提供串码 串码状态为空
  if (row.codeOptions !== 1) return { label: "", status: "noNeedImei" }

  const imeiYbStatus = row.imeiYbStatus
  const _sellStatus = row.sellStatus

  // 押宝查询结果未激活 根据拼多多查询结果判断最后状态
  if (imeiYbStatus === EActiveStatus.noActivate) {
    let statusObj = { ...(sellStatusObj[_sellStatus] || (imeiUploaded(row) ? verifyPassed : noUploaded)) }
    if (_sellStatus === sellStatus.alreadySold) return { ...statusObj, label: row.channels + statusObj.label }
    return statusObj
  }

  return { ...(EMActiveStatus[imeiYbStatus] || (imeiUploaded(row) ? { label: "已填写", status: "right" } : noUploaded)) }
}
export function getImeiStatusObj(row) {
  let key = `${row.codeOptions}-${row.imeiYbStatus}-${row.sellStatus}-${row.quantity}-${row.imeiNumber?.enteringNumber}-${row.channels}`
  if (!cacheStatus[key]) {
    let obj = getStatusObj(row)
    cacheStatus[key] = { ...obj, class: className[obj.status] || "" }
  }
  return cacheStatus[key]
}
