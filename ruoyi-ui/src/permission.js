import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import Layout from '@/layout/index'

function normalizeRoutePath(path) {
  return ('/' + path).replace(/\/+/g, '/')
}

function joinRoutePath(basePath, routePath) {
  if (!routePath) return normalizeRoutePath(basePath || '')
  if (routePath.startsWith('/')) return normalizeRoutePath(routePath)
  return normalizeRoutePath((basePath || '') + '/' + routePath)
}

function getFavoriteTabs() {
  const storeFavorites = store.state.tagsView.favoriteTabs || []
  if (storeFavorites.length > 0) return storeFavorites
  try {
    return JSON.parse(localStorage.getItem('layout-setting'))?.favoriteTabs || []
  } catch (e) {
    return []
  }
}

function findRoutePath(routes, predicate, basePath = '') {
  for (const route of routes) {
    if (route.path === '*' || route.path === '/404') continue
    const fullPath = joinRoutePath(basePath, route.path)
    if (!route.children || route.children.length === 0) {
      if (route.component !== Layout && predicate(fullPath, route)) return fullPath
    }
    const childPath = findRoutePath(route.children || [], predicate, fullPath)
    if (childPath) return childPath
  }
  return null
}

function findFavoritePath(routes) {
  const favorites = getFavoriteTabs()
  for (const favoritePath of favorites) {
    const normalizedFavorite = normalizeRoutePath(favoritePath)
    const matchedPath = findRoutePath(routes, path => path === normalizedFavorite)
    if (matchedPath) return matchedPath
  }
  return null
}

function resolveDefaultPath(routes) {
  return findFavoritePath(routes) || findFirstPath(routes)
}

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

// 递归查找第一个叶子路由的完整路径
function findFirstPath(routes) {
  for (const route of routes) {
    if (route.path === '*' || route.path === '/404') continue
    if (!route.children || route.children.length === 0) {
      // 跳过只有 Layout 组件的占位模块（如经营分析、工具箱），
      // 返回有实际页面组件的叶子路由路径
      if (route.component === Layout) continue
      return route.path
    }
    const childPath = findFirstPath(route.children)
    if (childPath) {
      return route.path + '/' + childPath
    }
  }
  return null
}

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()

  // 登录后如果跳转到 404（比如上次会话的页面已不存在），
  // 重定向到第一个有权限的菜单
  if (to.path === '/404' && getToken() && store.state.permission.addRoutes.length > 0) {
    const firstPath = findFirstPath(store.state.permission.addRoutes)
    if (firstPath) {
      next({ path: firstPath, replace: true })
      NProgress.done()
      return
    }
  }

  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            // 根路径无对应路由，重定向到有权限的第一个菜单
            if (to.path === '/') {
              const defaultPath = resolveDefaultPath(accessRoutes)
              next({ path: defaultPath || '/404', replace: true })
            } else {
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            }
          })
        }).catch(err => {
            store.dispatch('LogOut').finally(() => {
              Message.error(err)
              next('/login')
            })
          })
      } else if (to.path === '/') {
        // 角色已加载但目标是根路径（无对应路由），重定向到有权限的第一个菜单
        const routes = store.state.permission.addRoutes || []
        const defaultPath = resolveDefaultPath(routes)
        next({ path: defaultPath || '/404', replace: true })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
