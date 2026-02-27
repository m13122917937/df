import request from '@/utils/request'

// 检测手动改价是否需要提供原因
export function checkNeedReasonApi(data) {
  return request({
    url: `/rule/checkNeedReason`,
    method: "post",
    data
  })
}

//查询价格低于手动报价的系统规则
export function getSystemRules(data) {
  return request({
    url: `/rule/getSystemRules`,
    method: "post",
    data
  })
}

//获取快递规则列表
export function getLogisticList(skuId) {
  return request({
    url: `/rule/getLogisticList/${skuId}`,
    method: "get"
  })
}

//获取渠道规则列表
export function getChannelList(skuId) {
  return request({
    url: `/rule/getChannelList/${skuId}`,
    method: "get"
  })
}

//获取地区规则列表
export function getAreaRuleList(params) {
  return request({
    url: `/rule/getAreaList`,
    method: "get",
    params
  })
}

//渠道规则新增或修改
export function saveChannelRule(id, data) {
  return request({
    url: `/rule/channel`,
    method: "post",
    data: { ruleChannelForms: data, skuId: id }
  })
}

//地区规则新增或修改
export function saveAreaRule(data) {
  return request({
    url: "/rule/area",
    method: "post",
    data
  })
}

//快递规则新增或修改
export function saveLogisticRule(id, data) {
  return request({
    url: `/rule/logistic`,
    method: "post",
    data: { ruleLogisticForms: data, skuId: id }
  })
}

//获取规则列表
export function getPageRules(data) {
  return request({
    url: `/rule/pageRules`,
    method: "post",
    data
  })
}

//启用停用规则
export function changeRule(params) {
  return request({
    url: `/rule/enableOrDisable`,
    method: "put",
    params
  })
}

// 批量启用停用规则
export function ruleBatchEnableOrDisable(data) {
  return request({
    url: `/rule/batchEnableOrDisable`,
    method: "put",
    data
  })
}

//删除规则
export function deleteRule(id) {
  return request({
    url: `/rule/${id}`,
    method: "delete"
  })
}

//批量删除规则
export function batchDelete(data) {
  return request({
    url: `/rule/batchDelete`,
    method: "put",
    data
  })
}

//获取规则详情
export function getRuleInfo(id) {
  return request({
    url: `/rule/getRule/${id}`,
    method: "get"
  })
}

//添加编辑规则
export function saveRule(data) {
  return request({
    url: `/rule/saveRule`,
    method: "post",
    data
  })
}

// 发货时效规则查询
export function getRuleDeliveryList(skuId) {
  return request({
    url: `/rule/getRuleDeliveryList/${skuId}`,
    method: "get"
  })
}

// 发货时效规则新增或修改
export function saveDeliveryRule(data) {
  return request({
    url: `/rule/delivery`,
    method: "post",
    data
  })
}

export function ruleOrder(data) {
  return request({
    url: `/rule/ruleOrder`,
    method: "post",
    data
  })
}
export function getRuleOrderList(params) {
  return request({
    url: `/rule/getRuleOrderList`,
    method: "get",
    params
  })
}
// 查询昨日或今日定出价格的规则列表
export function getFinalPriceRecord(data) {
  return request({
    url: `/rule/getFinalPriceRecord`,
    method: "post",
    data
  })
}
