import request from '@/utils/request'

// 分页查询发票列表
export function pageInvoiceApi(query) {
  return request({
    url: '/finance/invoice/page',
    method: 'get',
    params: query,
  })
}

// 获取发票详情
export function getInvoiceApi(id) {
  return request({
    url: '/finance/invoice/' + id,
    method: 'get',
  })
}

// 新增发票
export function addInvoiceApi(data) {
  return request({
    url: '/finance/invoice',
    method: 'post',
    data,
  })
}

// 修改发票
export function updateInvoiceApi(data) {
  return request({
    url: '/finance/invoice',
    method: 'put',
    data,
  })
}

// 删除发票
export function removeInvoiceApi(id) {
  return request({
    url: '/finance/invoice/' + id,
    method: 'delete',
  })
}
