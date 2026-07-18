import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'
NProgress.configure({ showSpinner: false }) // NProgress Configuration
const whiteList = ['/login', '/auth-redirect'] // no redirect whitelist
router.beforeEach(async(to, from, next) => {
  NProgress.start()
  document.title = getPageTitle(to.meta.title)
  const hasToken = getToken()
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const owner = store.getters.owner
      if (owner === null || owner === undefined || owner === '') {
        await store.dispatch('user/resetToken')
        next(`/login`)
        NProgress.done()
        return
      }
      // 新增判断是否已经注入动态路由
      const hasRoutesInjected = store.state.permission && store.state.permission.routesInjected
      if ((owner || owner === 0) && hasRoutesInjected) {
        next()
      } else {
        try {
          // 确保owner有效，如果无效则使用默认值
          const validOwner = (owner !== null && owner !== undefined) ? owner : 1
          const accessRoutes = await store.dispatch('permission/generateRoutes', [validOwner])
          router.addRoutes(accessRoutes)
          // 注入完标记
          store.commit('permission/SET_ROUTES_INJECTED', true)
          next({ ...to, replace: true })
        } catch (error) {
          await store.dispatch('user/resetToken')
          Message.error(error || 'Has Error')
          next(`/login`)
          NProgress.done()
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login`)
      NProgress.done()
    }
  }
})
router.afterEach(() => {
  NProgress.done()
})
