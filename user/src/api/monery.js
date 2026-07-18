import request from '@/utils/request'

/**
 * 导出保证金交易记录
 * @param {Object} params
 * @returns
 */
export function exportTransactionRecords(params) {
  return request({
    url: '/company/capital/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取保证金信息
 * @returns
 */
export function getCapitalInfo() {
  return request({
    url: '/company/capital/info',
    method: 'get'
  })
}

/**
 * 获取保证金列表
 * @returns
 */
export function getCapitalList(data, pageData) {
  return request({
    url: `/company/capital/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 支付保证金
 * @param {Object} params
 * @returns
 */
export function payCapital(data, type) {
  return request({
    url: `/pay/${type}`,
    method: 'post',
    data
  })
}

/**
 * 获取支付状态
 * @param {Object} params
 * @returns
 */
export function getPayStatus(tradeNo) {
  return request({
    url: `/pay/status/${tradeNo}`,
    method: 'post'
  })
}

/**
 * 获取uuid
 * @param {Object} params
 * @returns
 */
export function getUuid(params) {
  return request({
    url: '/pay/uuid',
    method: 'get',
    data: params
  })
}

/**
 * 查询保证金类型
 * @param {Object} params
 * @returns
 */
export function getCapitalTypeInfo(params) {
  return request({
    url: '/company/capital/type/info',
    method: 'get',
    data: params
  })
}

/**
 * 查询收款详情
 */
export function getBillTodayList(data, pageData) {
  return request({
    url: `/bill/today/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 确定收款
 * @param {Object} params
 * @returns
 */
export function todayConfirm(id) {
  return request({
    url: `/bill/today/confirm/${id}`,
    method: 'get'
  })
}

/**
 * 收款异常
 * @param {Object} params
 * @returns
 */
export function todayError(id) {
  return request({
    url: `/bill/today/error/${id}`,
    method: 'get'
  })
}

/**
 * 导出明细
 * @param {Object} params
 * @returns
 */
export function todayExport(id) {
  return request({
    url: `/bill/today/export/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 获取银行账户列表
 * @returns
 */
export function getBankAccountList() {
  return request({
    url: '/company/bank/list',
    method: 'get'
  })
}

/**
 * 新增银行账户
 * @param {Object} data
 * @returns
 */
export function addBankAccount(data) {
  return request({
    url: '/company/bank/save',
    method: 'post',
    data
  })
}

/**
 * 获取省份列表
 * @returns
 */
export function getProvinceList() {
  return request({
    url: '/system/province/list',
    method: 'get'
  })
}

/**
 * 根据省份ID获取城市列表
 * @param {Number} provinceId
 * @returns
 */
export function getCityListByProvince(provinceId) {
  return request({
    url: `/system/city/${provinceId}`,
    method: 'get'
  })
}

