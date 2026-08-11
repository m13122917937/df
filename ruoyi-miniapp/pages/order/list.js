const { request } = require('../../utils/request')

Page({
  data: { orders: [] },
  onShow() { this.loadOrders() },
  loadOrders() { request({ url: '/miniapp/orders' }).then(orders => this.setData({ orders })) },
  pay(event) { wx.navigateTo({ url: '/pages/order/pay?orderNo=' + event.currentTarget.dataset.orderNo }) },
  cancel(event) { request({ url: `/miniapp/orders/${event.currentTarget.dataset.orderNo}/cancel`, method: 'POST' }).then(() => this.loadOrders()) },
  applyRefund(event) {
    wx.showModal({ title: '申请退款', content: '仅未发货订单支持整单退款，是否继续？', success: ({ confirm }) => {
      if (confirm) request({ url: `/miniapp/orders/${event.currentTarget.dataset.orderNo}/refunds`, method: 'POST', data: { reason: '用户申请退款' } })
        .then(() => { wx.showToast({ title: '退款申请已提交' }); this.loadOrders() })
    } })
  },
  confirmReceived(event) {
    wx.showModal({ title: '确认收货', content: '确认已收到商品？', success: ({ confirm }) => {
      if (confirm) request({ url: `/miniapp/orders/${event.currentTarget.dataset.orderNo}/confirm-received`, method: 'POST' })
        .then(() => { wx.showToast({ title: '已确认收货' }); this.loadOrders() })
    } })
  }
})
