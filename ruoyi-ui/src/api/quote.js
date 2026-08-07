import request from '@/utils/request'
import { download } from '@/utils/request'

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
 * 保存当天报价（报价每日维护，幂等覆盖当天）。
 *
 * @param {Object} data 商品ID与三档价格数据
 * @returns {Promise} 操作结果
 */
export function saveQuote(data) {
  return request({
    url: '/quote/quote/save',
    method: 'post',
    data
  })
}

/**
 * 查询商品历史报价。
 *
 * @param {number} productId 商品ID
 * @returns {Promise} 历史报价集合
 */
export function getQuoteHistory(productId) {
  return request({
    url: `/quote/quote/history/${productId}`,
    method: 'get'
  })
}

/**
 * 分页查询客户层级。
 *
 * @returns {Promise} 客户层级分页数据
 */
export function getQuoteCustomerLevelPage() {
  return request({
    url: '/quote/customer-level/page',
    method: 'post'
  })
}

/**
 * 保存客户层级。
 *
 * @param {Object} data 客户层级数据
 * @returns {Promise} 操作结果
 */
export function saveQuoteCustomerLevel(data) {
  return request({
    url: '/quote/customer-level/save',
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
 * 分页查询品牌。
 *
 * @param {Object} data 查询参数
 * @returns {Promise} 品牌分页数据
 */
export function getQuoteBrandPage(data) {
  return request({
    url: '/quote/brand/page',
    method: 'post',
    data
  })
}

/**
 * 查询全部品牌（下拉选项）。
 *
 * @returns {Promise} 品牌集合
 */
export function getQuoteBrandOptions() {
  return request({
    url: '/quote/brand/options',
    method: 'get'
  })
}

/**
 * 保存品牌。
 *
 * @param {Object} data 品牌数据
 * @returns {Promise} 操作结果
 */
export function saveQuoteBrand(data) {
  return request({
    url: '/quote/brand/save',
    method: 'post',
    data
  })
}

/**
 * 删除品牌。
 *
 * @param {number} id 品牌ID
 * @returns {Promise} 操作结果
 */
export function delQuoteBrand(id) {
  return request({
    url: `/quote/brand/${id}`,
    method: 'delete'
  })
}

/**
 * 分页查询品类。
 *
 * @param {Object} data 查询参数
 * @returns {Promise} 品类分页数据
 */
export function getQuoteCategoryPage(data) {
  return request({
    url: '/quote/category/page',
    method: 'post',
    data
  })
}

/**
 * 查询全部品类（下拉选项）。
 *
 * @returns {Promise} 品类集合
 */
export function getQuoteCategoryOptions() {
  return request({
    url: '/quote/category/options',
    method: 'get'
  })
}

/**
 * 保存品类。
 *
 * @param {Object} data 品类数据
 * @returns {Promise} 操作结果
 */
export function saveQuoteCategory(data) {
  return request({
    url: '/quote/category/save',
    method: 'post',
    data
  })
}

/**
 * 删除品类。
 *
 * @param {number} id 品类ID
 * @returns {Promise} 操作结果
 */
export function delQuoteCategory(id) {
  return request({
    url: `/quote/category/${id}`,
    method: 'delete'
  })
}

/**
 * 导出报价商品 Excel。
 *
 * @param {Object} data 查询参数
 * @returns {Promise} 导出结果
 */
export function exportQuoteProduct(data) {
  return download('/quote/product/export', data, '报价商品.xlsx')
}

/**
 * 导入报价商品 Excel。
 *
 * @param {FormData} formData 文件表单
 * @returns {Promise} 导入统计
 */
export function importQuoteProduct(formData) {
  return request({
    url: '/quote/product/import',
    method: 'post',
    data: formData
  })
}
