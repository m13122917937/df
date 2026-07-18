/** 财务管理路由模块 **/

import Layout from '@/layout'

const financeRouter = {
  path: '/finance',
  component: Layout,
  redirect: '/finance/index',
  name: 'Finance',
  meta: {
    title: '财务管理',
    owner: [0], // 0: 管理员可以访问
    icon: 'money'
  },
  children: [
    {
      path: 'index',
      component: () => import('@/views/finance/index'),
      name: 'FinanceIndex',
      meta: {
        title: '财务管理',
        noCache: true,
        owner: [0] // 0: 管理员可以访问
      }
    }
  ]
}

export default financeRouter
