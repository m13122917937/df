import defaultSettings from "@/settings"

const { sideTheme, showSettings, topNav, tagsView, fixedHeader, sidebarLogo, dynamicTitle, themeMode } = defaultSettings

const storageSetting = JSON.parse(localStorage.getItem("layout-setting")) || ""
const state = {
  title: "",
  theme: storageSetting.theme || "#5B7CFA",
  sideTheme: storageSetting.sideTheme || sideTheme,
  themeMode: storageSetting.themeMode || themeMode,
  showSettings: showSettings,
  topNav: storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
  tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
  fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
  sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
  dynamicTitle: storageSetting.dynamicTitle === undefined ? dynamicTitle : storageSetting.dynamicTitle
}
const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (Object.prototype.hasOwnProperty.call(state, key)) {
      state[key] = value
    }
  }
}

const actions = {
  // 修改布局设置
  changeSetting({ commit }, data) {
    commit("CHANGE_SETTING", data)
  },
  // 设置页面标题
  setTitle({ commit }, title) {
    commit("CHANGE_SETTING", { key: "title", value: title })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
