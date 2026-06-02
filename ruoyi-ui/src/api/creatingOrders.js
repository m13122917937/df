import request from '@/utils/request'

//
export function apiGetNewList(params) {
  return request({
    url: "/order/new/list/new",
    method: "GET",
    params
  })
}
export function apiGetDeliveryToday(params) {
  return request({
    url: "/order/new/list/delivery/today",
    method: "GET",
    params
  })
}

export function apiGetDelivery(params) {
  return request({
    url: "/order/new/list/delivery",
    method: "GET",
    params
  })
}
export function apiGetTrading(params) {
  return request({
    url: "/order/new/list/trading",
    method: "GET",
    params
  })
}

export function apiGetListWait(params) {
  return request({
    url: "/order/new/list/wait",
    method: "GET",
    params
  })
}


export function addErrorApi(params) {
  return request({
    url: `/order/error/error`,
    method: "get",
    params,
  });
}



export function apiGetRuleList(params) {
  return request({
    url: `/rule/list`,
    method: "get",
    params,
  });
}
export function apiDeleteRule(data) {
  return request({
    url: `/rule/del`,
    method: "DELETE",
    data:data
  });
}
//  批量撤销
export function apiChaseHanging(data) {
  return request({
    url: `/order/chase/hanging`,
    method: "POST",
    data:data
  });
}

// 启用规则
export function apiRuleEnable(data) {
  return request({
    url: `/rule/enable`,
    method: "PUT",
    data:data
  });
}

// 禁用规则
export function apiRuleDisable(data) {
  return request({
    url: `/rule/disable`,
    method: "PUT",
    data:data
  });
}

// 新增规则
export function apiRuleSave(data) {
  return request({
    url: `/rule/save`,
    method: "POST",
    data:data
  });
}
export function apiSkuList() {
  return request({
    url: `/rule/sku/list`,
    method: "GET",
  });
}


export function apiRuleUpdate(data) {
  return request({
    url: `/rule/update`,
    method: "PUT",
    data
  });
}
//  报价
export function apiQuotation(data) {
  return request({
    url: `/order/new/quotation`,
    method: "POST",
    data
  });
}

export function apiErrorImport(data) {
  return request({
    url: `/order/error/import`,
    method: "POST",
    data
  });
}

export function exportError(data) {
  return request({
    url: `/order/error/export`,
    method: "post",
    responseType: "blob",
    data,
  });
}

export function deliveryIngToTodayApi(data) {
  return request({
    url: `/order/error/delivery/today`,
    method: "post",
    data,
  });
}

export function apiGetImei(orderId) {
  return request({
    url: `/order/new/list/${orderId}/imei`,
    method: "GET",
  });
}

export function apiGetApply(orderCode) {
  return request({
    url: `/order/apply/${orderCode}`,
    method: "GET",
  });
}


export function apiGetApplyAgreement(applyId){
  return request({
    url: `/order/apply/agreement/${applyId}`,
    method: "GET",
  });
}
export function apiGetApplyFail(data){
  return request({
    url: `/order/apply/fail/`,
    method: "POST",
    data
  });
}

// 报价中订单导出
export function apiGetTradingExport(data) {
  return request({
    url: `/order/trading/export`,
    method: "POST",
    responseType: "blob",
    data
  });
}
// 当日发货订单导出
export function apiGetDeliveryExport(data) {
  return request({
    url: `/order/delivery/export`,
    method: "POST",
    responseType: "blob",
    data
  });
}
// 定向推单
export function apiOrderWaitPush(data) {
  return request({
    url: `/order/wait/push`,
    method: "POST",
    responseType: "blob",
    data
  });
}

// 添加售后订单
export function apiAddAfterSales(data) {
  return request({
    url: `/order/aftersales/add`,
    method: "POST",
    data
  });
}


// 新建采购订单导出
export function exportCreatingOrdersApi(data) {
  return request({
    url: "/order/new/list/export",
    method: "POST",
    data,
    responseType: "blob",
  });
}