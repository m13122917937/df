import router from "../../router/index"
import imgSrc from "@/plugins/imgSrc"
import { getProvince } from "./const"
import { getDictType } from "@/api/system/dict"
import { requestThrottle } from "@/utils/throttle"

const globalData = {
  state: {
    axiosArr: [],
    globalBags: 0,
    GlobalImgSrc: imgSrc(),
    areas: [],
    areasFour: [],
    brands: [],
    expressCompany: [], // 物流方式
    warehouseList: {}, // 无仓地址
    companyList: [], // 当前用户企业列表
    companyInfo: {}, // 当前企业信息
    pageActive: true,
    bodyHeight: document.documentElement.clientHeight,
    needRuleList: [], // 供应商可以进入采购商列表
    externalHsList: [], // 华盛列表
    expressCompanyList: [] // 物流公司列表,
  },
  mutations: {
    SET_GLOBALBAGS: (state, name) => (state.globalBags = name),
    SET_BAGSPAGE: (state, name) => {
      if (name === "need") router.push({ path: "/need/deposition" })
      // else if(name==='supplier') router.push({ path: "/supplier/deposition"})
    },
    SET_BODY_HEIGHT: (state, hei) => (state.bodyHeight = hei),
    SET_AREAS: (state, areas) => (state.areas = areas),
    SET_BRANDS: (state, brands) => (state.brands = brands),
    SET_COMPANY_LIST: (state, data) => (state.companyList = data), // 当前用户企业列表
    SET_COMPANY_INFO: (state, data) => (state.companyInfo = data), // 当前企业信息

    SET_AXIOS_ARR: (state, cancelAjax) => state.axiosArr.push(cancelAjax.cancel),
    SET_EXTERNAL_HS_LIST: (state, data) => {
      state.externalHsList = data
    },
    SET_EXPRESS_COMPANY_LIST: (state, data) => {
      state.expressCompanyList = data
    }
  },

  actions: {
    async GET_AREAS({ commit, state }, status = null) {
      if (!state.areas.length) {
        let areas = await getProvince(status)
        commit("SET_AREAS", areas)
      }
    },
    async GET_BRANDS({ commit, state }) {
      await requestThrottle(state.brands, async () => {
        let res = await getDictType("o_brand_type")
        if (res[0]) {
          let brands = (res[1] || []).map(o => {
            return { value: o.dictValue, label: o.dictLabel }
          })
          commit("SET_BRANDS", brands)
        }
      })
    },

    async GET_COMPANY_INFO({ dispatch, state }) {
      if (!Object.keys(state.companyInfo).length) {
        // let companyInfo = JSON.parse(localStorage.getItem('COMPANY_INFO') || '{}')
        // if(Object.keys(companyInfo).length) commit('SET_COMPANY_INFO',companyInfo)
        // else dispatch('UPDATE_COMPANY_INFO')
        dispatch("UPDATE_COMPANY_INFO")
      }
    },
    async GET_EXTERNAL_HS_LIST({ commit, state }) {
      if (!state.externalHsList.length) {
        const [_suc, data] = await getHUASHENGdata()
        commit("SET_EXTERNAL_HS_LIST", data || [])
      }
    },
    async GET_EXPRESS_COMPANY_LIST({ commit, state }) {
      await requestThrottle(state.expressCompanyList, async () => {
        const [_suc, data] = await getDictType("p_express_company")
        if (_suc) {
          const list = (data || []).map(o => {
            return { ...o, value: o.dictValue, label: o.dictLabel }
          })
          commit("SET_EXPRESS_COMPANY_LIST", list)
        }
      })
    }
  }
}
export default globalData
