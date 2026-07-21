import { getProvinceListApi } from '@/api/order'
const state = {
  provinceList: []
}

const mutations = {
  SET_PROVINCE_LIST: (state, list) => {
    state.provinceList = list
  }
}

const actions = {
  // 获取省份列表
  async getProvinceList({ commit }) {
    try {
      if (state.provinceList.length > 0) {
        return
      }
      // 这里可以调用API获取省份数据
      const response = await getProvinceListApi()
      if (response.code === 200) {
        commit('SET_PROVINCE_LIST', response.data)
      }
    } catch (error) {
      console.error('获取省份列表失败:', error)
    }
  }
}

const getters = {
  provinceList: state => state.provinceList
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
