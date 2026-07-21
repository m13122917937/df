import { getInfo } from '@/api/user'
import { getToken, setToken, removeToken, getOwner, setOwner, removeOwner } from '@/utils/auth'
import router, { resetRouter } from '@/router'

const state = {
  token: getToken(),
  name: '',
  owner: getOwner(),
  companyVOList: [],
  uuid: '',
  currentCompany: null
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_OWNER: (state, owner) => {
    state.owner = owner
    if (owner !== null && owner !== undefined) {
      setOwner(owner)
    } else {
      removeOwner()
    }
  },
  SET_COMPANY_LIST: (state, companyList) => {
    state.companyVOList = companyList
  },
  SET_UUID: (state, uuid) => {
    state.uuid = uuid
  },
  SET_CURRENT_COMPANY: (state, company) => {
    state.currentCompany = company
  }
}

const actions = {

  setAccessToken({ commit }, token) {
    commit('SET_TOKEN', token)
    setToken(token)
  },
  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        console.log('response', response)
        const { data } = response
        if (!data) {
          reject('Verification failed, please Login again.')
        }
        const { owner, userName, companyVOList = [], uuid } = data
        if (owner !== null && owner !== undefined) {
          commit('SET_OWNER', owner)
        }
        commit('SET_NAME', userName)
        commit('SET_COMPANY_LIST', companyVOList)
        commit('SET_UUID', uuid)
        const currentCompany = companyVOList.find(item => item.curr && item.curr === true)
        console.log('currentCompany', currentCompany)
        commit('SET_CURRENT_COMPANY', currentCompany)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state, dispatch }) {
    commit('SET_TOKEN', '')
    commit('SET_OWNER', null)
    removeToken()
    removeOwner()
    // 重置 permission 路由注入标记
    commit('permission/SET_ROUTES_INJECTED', false, { root: true })
    resetRouter()
    router.push({
      path: '/login'
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_COMPANY_LIST', [])
      commit('SET_UUID', '')
      commit('SET_CURRENT_COMPANY', null)
      commit('SET_OWNER', null)
      removeToken()
      removeOwner()
      // 重置 permission 路由注入标记
      commit('permission/SET_ROUTES_INJECTED', false, { root: true })
      resolve()
    })
  }
}

const getters = {
  token: state => state.token,
  name: state => state.name,
  owner: state => state.owner,
  companyVOList: state => state.companyVOList,
  uuid: state => state.uuid,
  currentCompany: state => state.currentCompany,
  hasMultipleCompanies: state => state.companyVOList.length > 1
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
