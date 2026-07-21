import request from '@/utils/request'

/**
 * 获取全部订单列表
 * @param {Object} allOrderForm - 订单查询表单
 * @param {string} allOrderForm.brand - 品牌
 * @param {string} allOrderForm.category - 品类
 * @param {Array<string>} allOrderForm.orderCodeList - 订单编码集合
 * @param {string} allOrderForm.sendEndTime - 发货时间-结束
 * @param {string} allOrderForm.sendStartTime - 发货时间-开始
 * @param {string} allOrderForm.signedEndTime - 收货时间-结束
 * @param {string} allOrderForm.signedStartTime - 收货时间-开始
 * @param {number} allOrderForm.status - 订单状态
 * @returns {Promise} API响应
 */
export function getOrderAllList(data, params) {
  return request({
    url: `/order/all/list?pageNum=${params.pageNum}&pageSize=${params.pageSize}`,
    method: 'post',
    data: data
  })
}

