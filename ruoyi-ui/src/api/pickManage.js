import request from "@/utils/request";


/**
 * 查询品牌数量
 * @param {*} params
 * @returns
 */
export function getPickBrandCount(params) {
  return request({
    url: `/warehousing/order/brand/count`,
    method: "get",
    params,
  });
}

/**
 * 查询订单列表
 * @param {*} data
 * @param {*} pagedata
 * @returns
 */
export function getPickList(data, pagedata) {
  return request({
    url: `/warehousing/order/list?pageNum=${pagedata.pageNum}&pageSize=${pagedata.pageSize}`,
    method: "post",
    data,
  });
}

/**
 * 拣货入仓
 * @param {*} data
 * @returns
 */
export function setPicking(data) {
  return request({
    url: `/warehousing/order/picking`,
    method: "post",
    data,
  });
}

/**
 * 完成拣货
 * @param {*} data
 * @returns
 */
export function setPicked(orderCode) {
  return request({
    url: `/warehousing/order/confirm/${orderCode}`,
    method: "post"
  });
}

/**
 * 查询仓库地址列表 
 * @param {*} params
 * @returns
 */
export function getWarehouseType(params) {
  return request({
    url: `/system/dict/data/type/warehouse?pageNum=1&pageSize=1000`,
    method: "get",
    params,
  });
}
