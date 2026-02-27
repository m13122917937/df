import request from "@/utils/request";


/**
 * @description: 获取省份列表
 * @param {*} params
 * @returns
 */
export function getBusinessProvinceListApi(params) {
  return request({
    url: "/system/province/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 获取城市列表
 * @param {*} params 省份id:provinceId
 * @returns
 */
export function getBusinessCityListApi(provinceId) {
  return request({
    url: "/system/city/" + provinceId,
    method: "GET",
  });
}


/**
 * @description: 获取企业列表
 * @param {*} data
 * @returns
 */
export function getBusinessCompanyListApi(data,pageData) {
  return request({
    url: `/company/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "POST",
    data,
  });
}

/**
 * @description: 新增企业
 * @param {*} data
 * @returns
 */
export function addBusinessCompanyApi(data) {
  return request({
    url: "/company/add",
    method: "POST",
    data,
  });
}

/**
 * @description: 编辑企业
 * @param {*} data
 * @returns
 */
export function editBusinessCompanyApi(data) {
  return request({
    url: "/company/update",
    method: "POST",
    data,
  });
}

/**
 * @description: 获取企业银行账户列表
 * @param {*} params 企业id:companyId
 * @returns
 */
export function getBusinessBankListApi(companyId) {
  return request({
    url: "/company/bank/list/" + companyId,
    method: "GET",
  });
}

/**
 * @description: 新增企业银行账户
 * @param {*} data
 * @returns
 */
export function addBusinessBankApi(data) {
  return request({
    url: "/company/bank/save",
    method: "POST",
    data,
  });
}

/**
 * @description: 编辑企业银行账户
 * @param {*} data
 * @returns
 */
export function editBusinessBankApi(data) {
  return request({
    url: "/company/bank/update",
    method: "PUT",
    data,
  });
}

/**
 * @description: 企业用户列表
 * @param {*} data
 * @returns
 */
export function getBusinessUserListApi(companyId,data) {
  return request({
    url: "/company/user/list/" + companyId,
    method: "GET",
    params: data,
  });
}

/**
 * @description: 新增企业用户
 * @param {*} data
 * @returns
 */
export function addBusinessUserApi(data) {
  return request({
    url: "/company/user/add",
    method: "POST",
    data,
  });
}

/**
 * @description: 删除企业用户
 * @param {*} userId 用户ID
 * @param {*} companyId 企业ID
 * @returns
 */
export function deleteBusinessUserApi(companyId,userId) {
  return request({
    url: "/company/" + companyId + "/" + userId,
    method: "DELETE",
  });
}


/**
 * @description: 修改企业用户是否主账号
 * @param {*} companyId 企业ID
 * @param {*} userId 用户ID
 * @param {*} owner 是否主账号
 * @returns
 */
export function updateBusinessUserOwnerApi(companyId,userId,owner) {
  return request({
    url: "/company/" + companyId + "/" + userId + "/" + owner,
    method: "PUT",
  });
}