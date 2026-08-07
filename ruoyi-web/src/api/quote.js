import request from '@/utils/request'

/**
 * 查询全部价格档位。
 *
 * @returns {Promise} 价格档位集合
 */
export function apiGetQuoteTierList() {
  return request({
    url: '/quote/web/tier/list',
    method: 'get'
  })
}

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
