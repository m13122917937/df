import request from '@/utils/request'

/**
 * 分页查询批发报价商品（含各档位价格）。
 *
 * @param {Object} data 查询参数
 * @param {Object} params 分页参数
 * @returns {Promise} 报价商品分页数据
 */
export function apiGetQuoteProductList(data, params) {
  return request({
    url: `/quote/web/product/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data
  })
}

/**
 * 查询品牌列表。
 *
 * @returns {Promise} 品牌列表
 */
export function apiGetQuoteBrandList() {
  return request({
    url: '/quote/web/brand/list',
    method: 'get'
  })
}

/**
 * 查询品类列表。
 *
 * @returns {Promise} 品类列表
 */
export function apiGetQuoteCategoryList() {
  return request({
    url: '/quote/web/category/list',
    method: 'get'
  })
}

/**
 * 查询商品历史报价。
 *
 * @param {number} productId 商品ID
 * @returns {Promise} 历史报价集合
 */
export function apiGetQuoteHistory(productId) {
  return request({
    url: `/quote/web/quote/history/${productId}`,
    method: 'get'
  })
}
