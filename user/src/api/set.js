import request from '@/utils/request'
// ====== 子账号管理相关接口 ======

// 获取子账号列表
export function getSubAccountList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 启用 / 禁用子账号
export function toggleSubAccountStatus(data) {
  return request({
    url: '/user/disable',
    method: 'put',
    data
  })
}

// 添加子账号
export function addSubAccount(data) {
  return request({
    url: '/user/member/add',
    method: 'post',
    data
  })
}

// 删除子账号
export function removeSubAccount(userId) {
  return request({
    url: `/user/${userId}`,
    method: 'delete'
  })
}

// 转让管理员
export function transferSubAccountAdmin(userId) {
  return request({
    url: `/user/master/${userId}`,
    method: 'put'
  })
}

export function updateBusinessUserOwnerApi(userId, owner) {
  return request({
    url: `/user/${userId}/${owner}`,
    method: 'put'
  })
}

// ====== 企业信息相关接口 ======

// 获取企业信息
export function getCompanyInfo() {
  return request({
    url: '/company/info',
    method: 'get'
  })
}

// 更新企业信息
export function updateCompanyInfo(data) {
  return request({
    url: '/company/info',
    method: 'put',
    data
  })
}

// 获取企业合同认证地址
export function getCompanyAuthUrl() {
  return request({
    url: '/company/auth',
    method: 'get'
  })
}

