import store from "@/store"

function getInfo() {
  return store.state.user.userInfo
}
function getId() {
  return getInfo().userId
}
function getPhone() {
  return getInfo().phone
}
function getCompanyInfo() {
  return getInfo().company || {}
}
function getRoles() {
  return getInfo().roles || []
}
function hasRule(str) {
  return getRoles().some(o => o.roleKey === str)
}
// function hasRules(str) {
//   return !!getRoles().length
// }
function isOnlyRule(str) {
  return hasRule(str) && getRoles().length === 1
}
function getCompanyId() {
  return getCompanyInfo().id
}
function getNickName() {
  return getCompanyInfo().nickName
}
function getCompanyName() {
  return getCompanyInfo().companyName
}
function getCompanyType() {
  return getCompanyInfo().type
}
function isNl() {
  return getCompanyId() === 3
}
function inCompanies(ids) {
  return ids.includes(getCompanyId())
} // 当前是否在指定公司
function getUserName() {
  return getInfo().nickName
}
function getUserId() {
  return getInfo().userId
}

export const userInfo = {
  getInfo,
  getId,
  getPhone,
  getCompanyType,
  getCompanyInfo,
  getCompanyId,
  getNickName,
  getCompanyName,
  getRoles,
  hasRule,
  isOnlyRule,
  isNl,
  inCompanies,
  getUserName,
  getUserId
}
