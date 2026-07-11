import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

// 递归查找第一个叶子路由的完整路径（跳过 redirect: noRedirect 的父级）
function findFirstPath(routes) {
  for (const route of routes) {
    if (route.path === '*' || route.path === '/404') continue
    if (!route.children || route.children.length === 0) continue
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
              const firstPath = findFirstPath(accessRoutes)
              next({ path: firstPath || '/404', replace: true })
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
        const firstPath = findFirstPath(routes)
        next({ path: firstPath || '/404', replace: true })
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
