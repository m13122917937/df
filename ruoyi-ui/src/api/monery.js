import request from "@/utils/request";

/**
 * @description: 获取收款主体列表（列表）
 * @param {*} params
 * @returns
 */
export function getPayerListApi(params) {
  return request({
    url: "/bill/payer/page/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 获取收款主体企业银行卡列表
 * @param {*} params
 * @returns
 */
export function getCompanyBankListApi(companyId) {
  return request({
    url: `/company/bank/list/${companyId}`,
    method: "GET",
  });
}

/**
 * @description: 添加收款主体
 * @param {*} data
 * @returns
 */
export function addPayerApi(data) {
  return request({
    url: "/bill/payer/save",
    method: "POST",
    data,
  });
}

/**
 * @description: 更新收款主体
 * @param {*} data
 * @returns
 */
export function updatePayerApi(data) {
  return request({
    url: "/bill/payer/update",
    method: "POST",
    data,
  });
}

/**
 * @description: 分页查询流水记录
 * @param {*} params
 * @returns
 */
export function getTransactionsListApi(params) {
  return request({
    url: "/bill/transaction/page/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 新增流水记录
 * @param {*} data
 * @returns
 */
export function addTransactionApi(data) {
  return request({
    url: "/bill/transaction/save",
    method: "POST",
    data,
  });
}


/**
 * @description: 流水记录
 * @param {*} data
 * @returns
 */
export function getTransactionListApi(data) {
  return request({
    url: "/bill/transaction/page/list",
    method: "POST",
    data,
  });
}

/**
 * @description: 删除流水记录
 * @param {*} data
 * @returns
 */
export function deleteTransactionApi(id) {
  return request({
    url: `/bill/transaction/del/${id}`,
    method: "DELETE",
  });
}

/**
 * @description: 更新流水记录
 * @param {*} data
 * @returns
 */
export function updateTransactionApi(data) {
  return request({
    url: "/bill/transaction/update",
    method: "POST",
    data,
  });
}

/**
 * @description: 导出流水记录
 * @param {*} data
 * @returns
 */
export function exportTransactionListApi(data) {
  return request({
    url: "/bill/transaction/page/list/export",
    method: "POST",
    responseType: "blob",
    data,
  });
}

/**
 * @description: 获取收款主体列表（全部列表，下拉）
 * @param {*} params
 * @returns
 */
export function getPayerAllListApi(params) {
  return request({
    url: "/bill/payer/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 获取付款主体对应的银行账户列表
 * @param {*} params
 * @returns
 */
export function getPayCompanyBankListApi(params) {
  return request({
    url: "/bill/payer/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 获取付款配置列表
 * @param {*} params
 * @returns
 */
export function getPayerConfigListApi(params) {
  return request({
    url: "/bill/payer/config/list",
    method: "GET",
    params,
  });
}

/**
 * @description: 添加付款配置
 * @param {*} data
 * @returns
 */
export function addPayerConfigApi(data) {
  return request({
    url: "/bill/payer/config/save",
    method: "POST",
    data,
  });
}

/**
 * @description: 更新付款配置
 * @param {*} data
 * @returns
 */
export function updatePayerConfigApi(data) {
  return request({
    url: "/bill/payer/config/update",
    method: "POST",
    data,
  });
}

/**
 * @description: 查询企业列表
 * @param {*} data
 * @returns
 */
export function getCompanyListApi(data,pageData) {
  return request({
    url: `/company/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "POST",
    data,
  });
}


/**
 * @description: 确认排款计划
 * @param {*} data
 * @returns
 */
export function getPaymentTodayPlanApi(data) {
  return request({
    url: "/bill/detail/plan",
    method: "POST",
    data,
  });
}

/**
 * @description: pageList
 * @param {*} data
 * @returns
 */
export function getDeductionListApi(data,pageData) {
  return request({
    url: `/bill/deduction/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "POST",
    data,
  });
}

/**
 * @description: save
 * @param {*} data
 * @returns
 */
export function getDeductionSaveApi(data) {
  return request({
    url: `/bill/deduction/save`,
    method: "POST",
    data,
  });
}

/**
 * @description: save
 * @param {*} data
 * @returns
 */
export function getDeductionApi(orderCode) {
  return request({
    url: `/bill/deduction/${orderCode}`,
    method: "get",
  });
}

/**
 * @description: revoke
 * @param {*} orderCode
 * @returns
 */
export function getDeductionRevokeApi(orderCode) {
  return request({
    url: `/bill/deduction/revoke/${orderCode}`,
    method: "get",
  });
}

/**
 * @description: 平台列表
 * @param {*} data
 * @returns
 */
export function getPayerConfigPlatformListApi(data) {
  return request({
    url: `/bill/payer/config/platform`,
    method: "POST",
    data,
  });
}



