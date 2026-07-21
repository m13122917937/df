import request from '@/utils/request'

/**
 * 省列表对应订单数据
 * @param {*} params
 * @returns
 */
export function getProvinceCountApi(params) {
  return request({
    url: `/order/province/${params.status}`,
    method: 'get',
    params
  })
}

/**
 * 省
 * @param {*} params
 * @returns
 */
export function getProvinceListApi(params) {
  return request({
    url: '/system/province/list',
    method: 'get',
    params
  })
}

/**
 *  品牌订单数量
 * @param {*} params
 * @returns
 */
export function getBrandCountApi(params) {
  return request({
    url: `/order/brand/${params.status}`,
    method: 'get',
    params
  })
}

/**
 * 查询快递公司编码
 * @param {*} data
 * @returns
 */
export function getExpressCompany(data) {
  return request({
    url: `/express/company/${data.orderCode}`,
    method: 'get'
  })
}

/**
 * 保存物流单号, 并且订阅快递信息
 * @param {*} data
 * @returns
 */
export function saveOrderLogistics(data) {
  return request({
    url: '/express/company/order',
    method: 'post',
    data
  })
}

/**
 * 查询待发货订单
 * @returns
 */
export function getIngList(data, params) {
  return request({
    url: `/order/delivery/ing/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 导出发货订单信息
 * @returns
 */
export function exportOrder(data, params) {
  return request({
    url: `/order/delivery/ing/list/export?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    responseType: 'blob',
    data
  })
}

/**
 * 导入发货订单信息
 * @returns
 */
export function importShippingInfo(data) {
  return request({
    url: `/order/delivery/ing/list/import`,
    method: 'post',
    data
  })
}

/**
 * 导出当日发货订单信息
 * @returns
 */
export function exportEndOrder(data, params) {
  return request({
    url: `/order/delivery/end/list/export?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    responseType: 'blob',
    data
  })
}

/**
 * 毁单
 * @returns
 */
export function cancelOrder(data) {
  return request({
    url: '/apply/cancel',
    method: 'post',
    data
  })
}

/**
 * 查询毁单详情
 * @param {*} orderCode
 * @returns
 */
export function getCancelDetail(orderCode) {
  return request({
    url: `/apply/${orderCode}`,
    method: 'get'
  })
}
/**
 * 查询快递信息回显
 * @param {*} orderCode
 * @returns
 */
export function getExpressCompanyInfoShow(orderCode) {
  return request({
    url: `/express/company/express/${orderCode}`,
    method: 'get'
  })
}
/**
 * 提交串码
 * @param {*} data
 * @returns
 */
export function imeiSave(data) {
  return request({
    url: 'imei/save',
    method: 'post',
    data,
    timeout: 30000 // 设置30秒超时
  })
}

/**
 * 查询当日发货订单
 * @param {*} data
 * @returns
 */
export function getEndList(data, params) {
  return request({
    url: `/order/delivery/end/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 查询在途订单
 * @param {*} data
 * @returns
 */
export function getTransitList(data, params) {
  return request({
    url: `/order/transit/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 查询异常订单
 * @param {*} data
 * @returns
 */
export function getErrorList(data, params) {
  return request({
    url: `/order/error/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 查询退货追单订单
 * @param {*} data
 * @returns
 */
export function getChaseList(data, params) {
  return request({
    url: `/order/chase/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 查询确认收货订单
 * @param {*} data
 * @returns
 */
export function getConfirmList(data, params) {
  return request({
    url: `/order/receipt/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 确认追单
 * @param {*} data
 * @returns
 */
export function confirmChase(orderCode) {
  return request({
    url: `/order/confirmRevoke/${orderCode}`,
    method: 'get'
  })
}

/**
 * 查询订单串码
 * @param {*} data
 * @returns
 */
export function getOrderImei(orderCode) {
  return request({
    url: `/imei/${orderCode}`,
    method: 'get'
  })
}

/**
 * 查询快递信息
 * @param {*} data
 * @returns
 */
export function getExpressCompanyInfo(data) {
  return request({
    url: `/express/company/info/${data.logisticsNo}`,
    method: 'get'
  })
}

/**
 * 查询售后订单
 * @param {*} data
 * @returns
 */
export function getAfterSalesList(data, params) {
  return request({
    url: `/order/afterSales/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 修改异常订单物流信息/手机号
 * @param {*} data
 * @returns
 */
export function updateOrderLogistics(data) {
  return request({
    url: '/order/error/change',
    method: 'post',
    data
  })
}
