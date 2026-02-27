import request from "@/utils/request";

export function getPaymentTodayListApi(params) {
  return request({
    url: "/bill/detail/today",
    method: "GET",
    params,
  });
}
// 合计数据
export function getPaymentTodaySumApi(params) {
  return request({
    url: "/bill/detail/today/sum",
    method: "GET",
    params,
  });
}

// 排款计划详情
// billType: 账单类型（1:批量采购，2:一件代发，3：接龙订单）
// supplierId: 供应商ID
// payCompanyId: 付款主体ID
export function getPaymentTodayDetailApi(params) {
  return request({
    url: "/bill/detail/detail",
    method: "GET",
    params,
  });
}
// 通过订单号，查询付款企业主体
export function getPaymentTodaySplitApi(data) {
  return request({
    url: "/bill/detail/split",
    method: "POST",
    data,
  });
}

// 确定排款
export function getPaymentTodayPlanApi(data) {
  return request({
    url: "/bill/detail/plan",
    method: "POST",
    data,
  });
}


// 撤销排款
export function getPaymentTodayRevokeApi(planId) {
  return request({
    url: `/bill/detail/revoke/${planId}`,
    method: "POST",
  });
}

// 更新排款
export function getPaymentTodayUpdateApi(data) {
  return request({
    url: `/bill/plan/update`,
    method: "POST",
    data
  });
}


// 付款详情导出
export function exportPaymentTodayDetailApi(params) {
  return request({
    url: "/bill/detail/detail/export",
    method: "GET",
    params,
    responseType: "blob",
  });
}