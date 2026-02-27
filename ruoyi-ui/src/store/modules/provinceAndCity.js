import { getProvinceListApi, getCityListApi } from "@/api/wholesale";

const provinceAndCity = {
  state: {
    provinceList: [],
    cityList: []
  },
  mutations: {
    SET_PROVINCE_LIST: (state, data) => (state.provinceList = data),
    SET_CITY_LIST: (state, data) => (state.cityList = data)
  },
  actions: {
    async GET_PROVINCE_LIST({ commit, state }) {
      if (state.provinceList.length === 0) {
        try {
          const res = await getProvinceListApi()
          if (res && res.code === 200) {
            commit('SET_PROVINCE_LIST', res.data || [])
          }
        } catch (error) {
          console.error('获取省份列表失败:', error)
        }
      }
    },
    async GET_CITY_LIST({ commit, state }, provinceId) {
        try {
          const res = await getCityListApi(provinceId)
          if (res && res.code === 200) {
            commit('SET_CITY_LIST', res.data || [])
          }
        } catch (error) {
          console.error('获取城市列表失败:', error)
        }
    }

  }
}
export default provinceAndCity