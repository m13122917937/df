/** 财务管理路由模块 **/

import Layout from '@/layout'

const moneryRouter = {
  path: '/monery',
  component: Layout,
  redirect: '/monery/earnest',
  meta: {
    owner: [1], // 只有主账号可以访问
    title: '财务管理',
    icon: 'money'
  },
  children: [
    {
      path: '',
      component: () => import('@/views/monery/index'),
      meta: {
        owner: [1], // 只有主账号可以访问
        title: '财务管理'
      },
      children: [
        {
          path: 'earnest',
          component: () => import('@/views/monery/earnest'),
          name: 'Earnest',
          meta: {
            title: '保证金',
            owner: [1] // 只有主账号可以访问
          }
        },
        {
          path: 'collection',
          component: () => import('@/views/monery/collection/index'),
          name: 'Collection',
          meta: {
            title: '收款信息',
            owner: [1]
          },
          redirect: '/monery/collection/payment',
          children: [
            {
              path: 'payment',
              component: () => import('@/views/monery/payment-details'),
              name: 'PaymentDetails',
              meta: {
                title: '收款详情',
                owner: [1]
              }
            },
            {
              path: 'add-account',
              component: () => import('@/views/monery/addAccount'),
              name: 'AddAccount',
              meta: {
                title: '收款账户',
                owner: [1]
              }
            }
          ]
        },
        {
          path: 'contract',
          component: () => import('@/views/monery/contract/index'),
          name: 'Contract',
          meta: {
            title: '合同',
            owner: [1]
          },
          redirect: '/monery/contract/sign',
          children: [
            {
              path: 'sign',
              component: () => import('@/views/monery/contract/components/sign-info'),
              name: 'SignInfo',
              meta: {
                title: '签署信息',
                owner: [1]
              }
            },
            {
              path: 'signed',
              component: () => import('@/views/monery/contract/components/signed'),
              name: 'Signed',
              meta: {
                title: '已签署',
                owner: [1]
              }
            }
          ]
        }
      ]
    }
  ]
}

export default moneryRouter
