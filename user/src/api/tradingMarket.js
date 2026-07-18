import request from '@/utils/request'
// 获取登录二维码
export function apiGetBrandList() {
  return request({
    url: '/index/tab',
    method: 'get'
  })
}

export function apiGetProvince(data) {
  return request({
    url: `/index/province`,
    method: 'post',
    data
  })
}
// 首页查询 product list
// 首页查询产品列表接口(apiGetProduct)的参数结构如下：
// {
//   city: 0,           // 城市ID，0表示不限城市
//   productName: '',   // 产品名称（可选，模糊搜索）
//   province: 0,       // 省份ID，0表示不限省份
//   skuName: '',       // SKU名称（可选，模糊搜索）
//   tabName: ''        // 品牌名/标签名（可选）
// }
export function apiGetProduct(data) {
  return request({
    url: `/index/tab/product`,
    method: 'POST',
    data
  })
}
// 左侧sku数据
export function apiGetProductSku(data) {
  return request({
    url: `/index/tab/product/sku`,
    method: 'POST',
    data
  })
}
// sku 表格数据
export function apiGetProductSkuTable(data) {
  return request({
    url: `/index/tab/product/sku/list?pageNum=${data.pageNum}&pageSize=${data.pageSize}`,
    method: 'POST',
    data
  })
}
// 获取全量省
export function apiGetProvinceList() {
  return request({
    url: `/system/province/list`,
    method: 'get'
  })
}

// sku,订单详情信息，弹窗
export function apiGetSkuOrderInfo(data) {
  return request({
    url: `/index/sku/${data.orderCode}/${data.hangingOrderId}`,
    method: 'get'
  })
}
// 交易市场抢单
export function apiTradeGrabOrder(data) {
  return request({
    url: `/index/trade`,
    method: 'post',
    data
  })
}
