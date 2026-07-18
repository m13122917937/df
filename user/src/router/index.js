import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/* Router Modules */
import orderRouter from './modules/order'
// import financeRouter from './modules/finance'  // 暂时隐藏财务模块
import moneryRouter from './modules/monery'
import setRouter from './modules/set'
// import tradingMarketRouter from './modules/trading-market'  // 已移至 constantRoutes

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/o2o',
    name: 'TradingMarket',
    meta: {
      owner: [0, 1], // 0: 管理员, 1: 非管理员都可以访问
      title: '交易市场',
      icon: 'shopping'
    },
    children: [
      {
        path: 'o2o',
        component: () => import('@/views/trading-market/o2o'),
        name: 'O2O',
        meta: {
          title: '交易市场',
          owner: [0, 1] // 0: 管理员, 1: 非管理员都可以访问
        }
      },
      {
        path: 'rules',
        component: () => import('@/views/rules/index'),
        name: 'Rules',
        hidden: true,
        meta: {
          title: '规则管理',
          owner: [0, 1] // 0: 管理员, 1: 非管理员都可以访问
        }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  /** 业务路由模块 **/
  orderRouter,
  // financeRouter,  // 暂时隐藏财务模块
  moneryRouter,
  setRouter,
  // tradingMarketRouter,  // 已移至 constantRoutes 作为首页

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
