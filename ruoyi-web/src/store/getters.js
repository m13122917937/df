const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  introduction: state => state.user.introduction,
  owner: state => state.user.owner,
  roles: state => {
    const owner = state.user.owner
    // 当 owner 为 0 时，返回包含 'admin' 的角色数组
    if (owner === 0) {
      return ['admin']
    }
    // 其他情况下返回空数组或普通用户角色
    return []
  },
  currentCompany: state => state.user.currentCompany,
  companyVOList: state => state.user.companyVOList,
  permission_routes: state => state.permission.routes,
  errorLogs: state => state.errorLog.logs,
  provinceList: state => state.province.provinceList
}
export default getters
