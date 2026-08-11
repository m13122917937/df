import request from '@/utils/request'

export function getSubsidyOverview() {
  return request({ url: '/subsidy/overview', method: 'get' })
}

export function getSubsidyProducts(params) {
  return request({ url: '/subsidy/products', method: 'get', params })
}

export function addSubsidyProduct(data) {
  return request({ url: '/subsidy/products', method: 'post', data })
}

export function updateSubsidyProduct(id, data) {
  return request({ url: '/subsidy/products/' + id, method: 'put', data })
}

export function getSubsidyOrders(params) {
  return request({ url: '/subsidy/orders', method: 'get', params })
}

export function shipSubsidyOrder(orderNo, data) {
  return request({ url: '/subsidy/orders/' + orderNo + '/shipment', method: 'post', data })
}

export function getSubsidyRefunds(params) {
  return request({ url: '/subsidy/refunds', method: 'get', params })
}

export function approveSubsidyRefund(refundNo) {
  return request({ url: '/subsidy/refunds/' + refundNo + '/approve', method: 'post' })
}

export function getSubsidyCategories() {
  return request({ url: '/subsidy/categories', method: 'get' })
}

export function saveSubsidyCategory(data) {
  return request({ url: '/subsidy/categories', method: 'post', data })
}

export function updateSubsidyCategory(categoryId, data) {
  return request({ url: '/subsidy/categories/' + categoryId, method: 'put', data })
}

export function getSubsidyBanners() {
  return request({ url: '/subsidy/banners', method: 'get' })
}

export function saveSubsidyBanner(data) {
  return request({ url: '/subsidy/banners', method: 'post', data })
}

export function updateSubsidyBanner(bannerId, data) {
  return request({ url: '/subsidy/banners/' + bannerId, method: 'put', data })
}

export function getWechatIdentityConflicts(params) {
  return request({ url: '/subsidy/wechat-identity-conflicts', method: 'get', params })
}

export function getSubsidySkus(productId) {
  return request({ url: '/subsidy/skus', method: 'get', params: { productId } })
}

export function saveSubsidySku(data) {
  return request({ url: '/subsidy/skus', method: 'post', data })
}

export function updateSubsidySku(skuId, data) {
  return request({ url: '/subsidy/skus/' + skuId, method: 'put', data })
}

export function adjustSubsidyInventory(skuId, data) {
  return request({ url: '/subsidy/skus/' + skuId + '/inventory-adjustments', method: 'post', data })
}
