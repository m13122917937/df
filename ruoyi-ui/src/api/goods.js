import request from '@/utils/request'

export function apiSkuList() {
  return request({
    url: `/rule/sku/list`,
    method: "GET",
  });
}
// 品牌
export function apiProductBrand() {
  return request({
    url: `/product/brand`,
    method: "GET",
  });
}

// 品类
export function apiProductCategory(params) {
  return request({
    url: `/product/category`,
    method: "GET",
    params
  });
}


// brand and categroy 下面的product
export function apiGetProduct(params) {
  return request({
    url: `/product/product`,
    method: "GET",
    params
  });
}

// sku
export function apiGetProductSku(params) {
  return request({
    url: `/product/product/sku`,
    method: "GET",
    params
  });
}


// sku
export function apiGetProductInfo(params) {
  return request({
    url: `/product/info`,
    method: "GET",
    params
  });
}
// 查询今天成交数据
// 订单编号	 orderCode  省份 province

export function apiGetTradeListToday(params) {
  return request({
    url: `/trade/list/today`,
    method: "GET",
    params
  });
}
// 查询昨天天成交数据
// 订单编号	 orderCode  省份 province
export function apiGetTradeListYesterday(params) {
  return request({
    url: `/trade/list/yesterday`,
    method: "GET",
    params
  });
}
