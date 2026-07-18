import request from '@/utils/request'

/**
 * 合同类型枚举
 * 1 - 采购合同
 * 2 - 框架协议
 */
export const CONTRACT_TYPE_MAP = {
  1: '采购合同',
  2: '框架协议'
}

/**
 * 合同状态枚举
 * 0 - 草稿
 * 1 - 签署中
 * 2 - 已完成
 * 3 - 已拒签
 * 4 - 已过期
 * 5 - 已撤销
 */
export const CONTRACT_STATUS_MAP = {
  0: '草稿',
  1: '签署中',
  2: '已完成',
  3: '已拒签',
  4: '已过期',
  5: '已撤销'
}

export const CONTRACT_STATUS_TAG_MAP = {
  0: 'info',
  1: 'warning',
  2: 'success',
  3: 'danger',
  4: 'info',
  5: 'danger'
}

/**
 * 分页查询合同列表
 * @param {Object} data 查询条件
 * @param {number} data.pageNum 页码（从 1 开始）
 * @param {number} data.pageSize 每页条数
 * @param {string} [data.contractName] 合同名称模糊匹配
 * @param {number[]} [data.contractTypeList] 合同类型集合
 * @param {number[]} [data.statusList] 状态集合
 * @returns
 */
export function listContract(data) {
  return request({
    url: '/contract/list',
    method: 'post',
    data
  })
}

/**
 * 查询合同详情
 * @param {number|string} id 合同主键
 * @returns
 */
export function getContract(id) {
  return request({
    url: `/contract/${id}`,
    method: 'get'
  })
}

/**
 * 获取合同签署 URL（status=1 签署中可用）
 * @param {number|string} id 合同主键
 * @returns
 */
export function getContractSignUrl(id) {
  return request({
    url: `/contract/${id}/sign-url`,
    method: 'get'
  })
}
