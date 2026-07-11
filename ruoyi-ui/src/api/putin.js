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

/**
 * 下载导入模板
 */
export function downloadImportTemplate() {
  return request({
    url: `/warehousing/order/import/template`,
    method: "get",
    responseType: "blob",
  });
}

/**
 * 校验导入文件
 * @param {File} file
 */
export function importValidate(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: `/warehousing/order/import/validate`,
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}

/**
 * 导入文件并创建订单
 * @param {File} file
 */
export function importExcel(file) {
  const formData = new FormData();
  formData.append("file", file);
  return request({
    url: `/warehousing/order/import`,
    method: "post",
    data: formData,
    headers: { "Content-Type": "multipart/form-data" },
  });
}
