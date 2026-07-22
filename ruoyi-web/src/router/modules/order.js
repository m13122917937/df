/** 订单管理路由模块 **/

import Layout from '@/layout'

const orderRouter = {
  path: '/order',
  component: Layout,
  redirect: '/order/send',
  meta: {
    owner: [0, 1], // 0: 非主账号, 1: 主账号都可以访问
    title: '订单管理',
    icon: 'shopping'
  },
  children: [
    {
      path: '',
      component: () => import('@/views/order/index'),
      meta: {
        owner: [0, 1], // 0: 非主账号, 1: 主账号都可以访问,
        title: '订单管理'
      },
      children: [
        {
          path: 'send',
          component: () => import('@/views/order/pending'),
          name: 'PendingOrder',
          meta: {
            title: '待发货',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'today',
          component: () => import('@/views/order/today-shipped'),
          name: 'TodayShippedOrder',
          meta: {
            title: '当日发货',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'transit',
          component: () => import('@/views/order/transit'),
          name: 'TransitOrder',
          meta: {
            title: '在途',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'error',
          component: () => import('@/views/order/exception'),
          name: 'ExceptionOrder',
          meta: {
            title: '异常订单',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'return',
          component: () => import('@/views/order/refund'),
          name: 'RefundOrder',
          meta: {
            title: '退货追单',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'received',
          component: () => import('@/views/order/collected'),
          name: 'CollectedOrder',
          meta: {
            title: '确认收货',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'aftersale',
          component: () => import('@/views/order/after-sales'),
          name: 'AfterSalesOrder',
          meta: {
            title: '售后',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        },
        {
          path: 'all',
          component: () => import('@/views/order/all'),
          name: 'AllOrder',
          meta: {
            title: '全部订单',
            owner: [0, 1] // 0: 非主账号, 1: 主账号都可以访问
          }
        }
      ]
    }
  ]
}

export default orderRouter
