import request from '@/utils/request'

/**
 * 分页查询报价价格档位。
 *
 * @param {Object} data 查询参数
 * @returns {Promise} 价格档位分页数据
 */
export function getQuoteTierPage(data) {
  return request({
    url: '/quote/tier/list',
    method: 'post',
    data
  })
}

/**
 * 查询全部价格档位（下拉选项）。
 *
 * @returns {Promise} 价格档位集合
 */
export function getQuoteTierOptions() {
  return request({
    url: '/quote/tier/options',
    method: 'get'
  })
}

/**
 * 保存价格档位。
 *
 * @param {Object} data 档位数据
 * @returns {Promise} 操作结果
 */
export function saveQuoteTier(data) {
  return request({
    url: '/quote/tier/save',
    method: 'post',
    data
  })
}

/**
 * 删除价格档位。
 *
 * @param {number} id 档位ID
 * @returns {Promise} 操作结果
 */
export function delQuoteTier(id) {
  return request({
    url: `/quote/tier/${id}`,
    method: 'delete'
  })
}

/**
 * 分页查询报价商品。
 *
 * @param {Object} data 查询参数
 * @returns {Promise} 报价商品分页数据
 */
export function getQuoteProductPage(data) {
  return request({
    url: '/quote/product/page',
    method: 'post',
    data
  })
}

/**
 * 保存报价商品（含各档位价格）。
 *
 * @param {Object} data 商品数据
 * @returns {Promise} 操作结果
 */
export function saveQuoteProduct(data) {
  return request({
    url: '/quote/product/save',
    method: 'post',
    data
  })
}

/**
 * 删除报价商品。
 *
 * @param {number} id 商品ID
 * @returns {Promise} 操作结果
 */
export function delQuoteProduct(id) {
  return request({
    url: `/quote/product/${id}`,
    method: 'delete'
  })
}

/**
 * 查询品牌列表。
 *
 * @returns {Promise} 品牌列表
 */
export function getQuoteBrands() {
  return request({
    url: '/quote/product/brands',
    method: 'get'
  })
}

/**
 * 查询品类列表。
 *
 * @returns {Promise} 品类列表
 */
export function getQuoteCategories() {
  return request({
    url: '/quote/product/categories',
    method: 'get'
  })
}
