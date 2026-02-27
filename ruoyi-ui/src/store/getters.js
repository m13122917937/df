const getters = {
  sidebar: (state) => state.app.sidebar,
  size: (state) => state.app.size,
  device: (state) => state.app.device,
  visitedViews: (state) => state.tagsView.visitedViews,
  cachedViews: (state) => state.tagsView.cachedViews,
  token: (state) => state.user.token,
  avatar: (state) => state.user.avatar,
  name: (state) => state.user.name,
  nickName: (state) => state.user.nickName,
  introduction: (state) => state.user.introduction,
  roles: (state) => state.user.roles,
  permissions: (state) => state.user.permissions,
  permission_routes: (state) => state.permission.routes,
  topbarRouters: (state) => state.permission.topbarRouters,
  defaultRoutes: (state) => state.permission.defaultRoutes,
  sidebarRouters: (state) => state.permission.sidebarRouters,
  bodyHeight: (state) => state.globalData.bodyHeight,
  isBD: (state) => state.user.isBD,
  // 全国
  provinceAllList: (state) => {
    const provinceList = state.globalData.areas;
    if (!Array.isArray(provinceList)) return [];
    let list = JSON.parse(JSON.stringify(provinceList));
    let area = list.find((o) => o.value === 1);
    if (area) area.label = "全国";
    return list;
  },
  // 是否是醒市
  isGuaranteeSale: (state) => {
    const companyId = state.user.userInfo?.company?.id;
    const companyIds = [4];
    return companyIds.includes(companyId);
  },
  // 群接龙集采列表 根据公司展示不同 醒市展示备货
  // 醒市 添加散货集采
  collectivePurchaseTypeList: (_state, getters) => {
    const isGuaranteeSale = getters.isGuaranteeSale;
    let list = [
      { label: "履约集采", value: 1 },
      { label: "备货集采", value: 2 },
    ];
    if (isGuaranteeSale) {
      list = [
        ...list,
        { label: "保卖集采", value: 3 },
        { label: "散货集采", value: 4 },
      ];
    }
    return list;
  },
  expressCompanyList: (state) => state.globalData.expressCompanyList,
  commodityInfo: (state) => state.commodity.commodityInfo,
  updateCommodityPic: (state) => state.commodity.updateCommodityPic,
  vendorAuditNumber: (state) => state.vendorAudit.vendorAuditNumber,

  // 省市
  provinceList: (state) => state.provinceAndCity.provinceList,
  cityList: (state) => state.provinceAndCity.cityList,
};
export default getters
