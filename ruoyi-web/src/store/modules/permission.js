import { asyncRoutes, constantRoutes } from '@/router'

function hasPermission(owners, route) {
  console.log('owners,,,', owners, 'route.meta.owner:', route.meta ? route.meta.owner : 'no meta')
  if (route.meta && route.meta.owner) {
    // 检查owners数组中是否有任何一个值在route.meta.owner数组中
    const hasPermission = owners.some(owner => route.meta.owner.includes(owner))
    console.log('hasPermission result:', hasPermission)
    return hasPermission
  } else {
    return true
  }
}

export function filterAsyncRoutes(routes, owners) {
  console.log('filterAsyncRoutes,,,', owners)
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(owners, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, owners)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: constantRoutes,
  addRoutes: [],
  routesInjected: false // 标记动态路由是否已注入
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  },
  SET_ROUTES_INJECTED(state, status) {
    state.routesInjected = status
  }
}

const actions = {
  generateRoutes({ commit }, owners) {
    return new Promise(resolve => {
      let accessedRoutes
      // 如果owners数组包含0（管理员），则拥有所有路由权限
      if (owners && owners.includes && owners.includes(0)) {
        accessedRoutes = asyncRoutes || []
      } else {
        // 否则根据owners数组过滤路由
        accessedRoutes = filterAsyncRoutes(asyncRoutes, owners)
        console.log('accessedRoutes,,,', accessedRoutes)
      }
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
