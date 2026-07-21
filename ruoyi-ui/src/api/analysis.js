import request from '@/utils/request'

export function getAnalysisDashboard(type, params) {
  return request({
    url: `/analysis/dashboard/${type}`,
    method: 'get',
    params
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

export function getAnalysisSyncLogs() {
  return request({
    url: '/analysis/sync/logs',
    method: 'get'
  })
}

export function getAnalysisRebateList() {
  return request({ url: '/analysis/rebate/list', method: 'get' })
}

export function saveAnalysisRebate(data) {
  return request({ url: '/analysis/rebate/save', method: 'post', data })
}

export function deleteAnalysisRebate(id) {
  return request({ url: `/analysis/rebate/${id}`, method: 'delete' })
}
