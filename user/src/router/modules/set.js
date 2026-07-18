/** 基础配置路由模块 **/

import Layout from '@/layout'

const setRouter = {
  path: '/set',
  component: Layout,
  redirect: '/set/user',
  meta: {
    owner: [1], // 只有主账号可以访问
    title: '基础配置',
    icon: 'setting'
  },
  children: [
    {
      path: '',
      component: () => import('@/views/set/index'),
      meta: {
        owner: [1], // 只有主账号可以访问
        title: '基础配置'
      },
      children: [
        {
          path: 'user',
          component: () => import('@/views/set/user'),
          name: 'User',
          meta: {
            title: '用户管理',
            owner: [1] // 只有主账号可以访问
          }
        }
      ]
    }
  ]
}

export default setRouter
