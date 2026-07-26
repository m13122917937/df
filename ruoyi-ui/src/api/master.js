import request from '@/utils/request'

/**
 * 查询经营主体主数据。
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 经营主体分页数据
 */
export function getMasterSubjectList(params) {
  return request({
    url: '/master/subject/list',
    method: 'get',
    params
  })
}

/**
 * 查询销售渠道主数据。
 *
 * @param {Object} params 查询参数
 * @returns {Promise} 销售渠道分页数据
 */
export function getMasterSalesChannelList(params) {
  return request({
    url: '/master/sales-channel/list',
    method: 'get',
    params
  })
}

/**
 * 查询主体下的银行卡列表及当前默认卡。
 *
 * @param {number} subjectId 经营主体ID
 * @returns {Promise} 银行卡列表响应
 */
export function getSubjectBankList(subjectId) {
  return request({
    url: `/master/subject/bank/list/${subjectId}`,
    method: 'get'
  })
}

/**
 * 设置主体默认银行卡。
 *
 * @param {number} subjectId 经营主体ID
 * @param {number} payerId 银行卡ID
 * @returns {Promise} 操作结果
 */
export function setSubjectDefaultBank(subjectId, payerId) {
  return request({
    url: `/master/subject/bank/default/${subjectId}/${payerId}`,
    method: 'put'
  })
}

/**
 * 查询经营主体下拉选项（仅正常状态）。
 *
 * @returns {Promise} 经营主体集合
 */
export function getSubjectOptionList() {
  return request({
    url: '/master/subject/list',
    method: 'get',
    params: {
      pageNum: 1,
      pageSize: 10000,
      isDelete: 0
    }
  })
}
