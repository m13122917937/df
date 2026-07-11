import request from '@/utils/request'

/**
 * 分页查询销售退货列表
 */
export function listSalesReturnApi(pageData, data) {
  return request({
    url: `/sales/return/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "post",
    data,
  });
}

/**
 * 新增销售退货单
 */
export function saveSalesReturnApi(data) {
  return request({
    url: `/sales/return/save`,
    method: "post",
    data,
  });
}

/**
 * 根据内部单号或商家单号查询订单详情（自动带出）
 */
export function getOrderDetailApi(params) {
  return request({
    url: `/sales/return/order-detail`,
    method: "get",
    params,
  });
}

/**
 * 查询退货单详情
 */
export function getSalesReturnApi(id) {
  return request({
    url: `/sales/return/${id}`,
    method: "get",
  });
}
