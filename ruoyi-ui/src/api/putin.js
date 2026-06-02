import request from "@/utils/request";


/**
 * 查询品牌数量
 * @param {*} params
 * @returns
 */
export function getPutinBrandCount(params) {
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
export function getPutinList(data, pagedata) {
  return request({
    url: `/warehousing/order/list?pageNum=${pagedata.pageNum}&pageSize=${pagedata.pageSize}`,
    method: "post",
    data,
  });
}

/**
 * 查询品牌列表
 * @param {*} data
 * @returns
 */
export function getProductBrandList(params) {
  return request({
    url: `/product/brand`,
    method: "get",
    params,
  });
}


/**
 * 根据品牌查询商品名称列表
 * @param {*} params
 * @returns
 */
export function getProductNameList(params) {
  return request({
    url: `/product/productName`,
    method: "get",
    params,
  });
}

/**
 * 分页查询商品SKU列表
 * @param {*} data
 * @param {*} pagedata
 * @returns
 */
export function getSkuList(data, pagedata={
  pageNum:1,
  pageSize:1000
}) {
  return request({
    url: `/product/sku/page/list?pageNum=${pagedata.pageNum}&pageSize=${pagedata.pageSize}`,
    method: "post",
    data,
  });
}

/**
 * 入仓保存订单
 * @param {*} data 
 * @returns 
 */
export function saveOrder(data) {
  return request({
    url: `/warehousing/order/save`,
    method: "post",
    data,
  });
}

/**
 * 撤销
 */
export function putinRevokeOrder(orderCode) {
  return request({
    url: `/warehousing/order/revoke/${orderCode}`,
    method: "post"
  });
}

/**
 * 修改物流信息
 */
export function updateTracking(data) {
  return request({
    url: `/warehousing/order/tracking`,
    method: "post",
    data,
  });
}
