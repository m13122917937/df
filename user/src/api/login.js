import request from '@/utils/request'
// 获取登录二维码
export function getLoginQrCode() {
  return request({
    url: '/user/loginQrCode',
    method: 'get'
  })
}

// 检查登录二维码状态
export function checkLoginQrCode(uuid) {
  return request({
    url: `/user/loginStatus/${uuid}`,
    method: 'get'
  })
}

// 获取登录信息
export function getLoginInfo() {
  return request({
    url: '/user/login/info',
    method: 'get'
  })
}

// 切换企业，获取token
export function getLoginToken(uuid, companyId) {
  return request({
    url: `/user/login/${uuid}/${companyId}`,
    method: 'get'
  })
}

// 切换企业
export function changeCompany(companyId) {
  return request({
    url: `/user/login/${companyId}`,
    method: 'get'
  })
}
