import request from '@/utils/request'

export function getAnalysisDashboard(type, params) {
  return request({
    url: `/analysis/dashboard/${type}`,
    method: 'get',
    params
  })
}

export function getAnalysisDashboardFilterOptions() {
  return request({
    url: '/analysis/dashboard/filter-options',
    method: 'get'
  })
}

export function getAnalysisConfigList(params) {
  return request({
    url: '/analysis/config/list',
    method: 'get',
    params
  })
}

export function getAnalysisMarginList(params) {
  return request({ url: '/analysis/margin/list', method: 'get', params })
}

export function saveAnalysisMargin(data) {
  return request({ url: '/analysis/margin/save', method: 'post', data })
}

export function deleteAnalysisMargin(id) {
  return request({ url: `/analysis/margin/${id}`, method: 'delete' })
}

export function getAnalysisCollectionCycleList(params) {
  return request({ url: '/analysis/collection-cycle/list', method: 'get', params })
}

export function getAnalysisStoreOptions() {
  return request({ url: '/analysis/store-options', method: 'get' })
}

export function saveAnalysisCollectionCycle(data) {
  return request({ url: '/analysis/collection-cycle/save', method: 'post', data })
}

export function getAnalysisWarehouseCostList(params) { return request({ url: '/analysis/warehouse-cost/list', method: 'get', params }) }
export function saveAnalysisWarehouseCost(data) { return request({ url: '/analysis/warehouse-cost/save', method: 'post', data }) }

export function saveAnalysisConfig(data) {
  return request({
    url: '/analysis/config/save',
    method: 'post',
    data
  })
}

export function deleteAnalysisConfig(id) {
  return request({
    url: `/analysis/config/${id}`,
    method: 'delete'
  })
}

export function importAnalysisConfig(configType, overwrite, file) {
  const data = new FormData()
  data.append('file', file)
  return request({
    url: '/analysis/config/import',
    method: 'post',
    params: { configType, overwrite },
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getAnalysisImportLogs(limit) {
  return request({
    url: '/analysis/config/import/logs',
    method: 'get',
    params: { limit }
  })
}

export function runAnalysisSync(date) {
  return request({
    url: '/analysis/sync/run',
    method: 'post',
    params: { date }
  })
}

export function rebuildAnalysis(date) {
  return request({
    url: '/analysis/calculate/rebuild',
    method: 'post',
    params: { date }
  })
}

// 平台服务费率
export function getPlatformFeeRateList(params) {
  return request({ url: '/analysis/platform-fee-rate/list', method: 'get', params })
}

export function savePlatformFeeRate(data) {
  return request({ url: '/analysis/platform-fee-rate/save', method: 'post', data })
}

export function deletePlatformFeeRate(id) {
  return request({ url: `/analysis/platform-fee-rate/${id}`, method: 'delete' })
}

export function importPlatformFeeRate(overwrite, file) {
  const data = new FormData()
  data.append('file', file)
  return request({
    url: '/analysis/platform-fee-rate/import',
    method: 'post',
    params: { overwrite },
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function exportPlatformFeeRate(params) {
  return request({
    url: '/analysis/platform-fee-rate/export',
    method: 'post',
    params,
    responseType: 'blob'
  })
}

export function downloadPlatformFeeRateTemplate() {
  return request({
    url: '/analysis/platform-fee-rate/template',
    method: 'post',
    responseType: 'blob'
  })
}
