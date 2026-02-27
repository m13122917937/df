import store from "@/store"

function getInfo() {
  return store.state.user.userInfo || {}
}
function getId() {
  return getInfo().userId
}
function getCompanyInfo() {
  return getInfo().company || {}
}
function getCompanyProvinceFlag() {
  return getCompanyInfo().provinceFlag
} // 是否省分公司 0-否  1-是
function getCompanyId() {
  return getCompanyInfo().id
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
function isXs() {
  return getCompanyId() === 4
}
function isNlAndProvince() {
  return getCompanyProvinceFlag() || isNl()
}
function inCompanies(ids) {
  return ids.includes(getCompanyId())
} // 当前是否在指定公司
function getUserName() {
  return getInfo().nickName
}
export const userInfo = {
  getInfo,
  getId,
  getCompanyType,
  getCompanyInfo,
  getCompanyId,
  getCompanyName,
  isNl,
  inCompanies,
  isXs,
  isNlAndProvince,
  getUserName
}
