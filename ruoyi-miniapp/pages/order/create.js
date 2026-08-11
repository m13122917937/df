const { request } = require('../../utils/request')

Page({
  data: { skuId: null, quantity: 1, addresses: [], address: null },
  onLoad({ skuId, quantity }) { this.setData({ skuId, quantity: Number(quantity || 1) }) },
  onShow() { const selected = wx.getStorageSync('miniappSelectedAddress'); request({ url: '/miniapp/addresses' }).then(addresses => { const address = selected || addresses.find(item => item.defaultAddress === 1) || addresses[0]; this.setData({ addresses, address }); wx.removeStorageSync('miniappSelectedAddress') }) },
  addAddress() { wx.navigateTo({ url: '/pages/address/edit' }) },
  selectAddress() { wx.navigateTo({ url: '/pages/address/index?select=1' }) },
  submit() {
    const address = this.data.address
    if (!address) { wx.showToast({ title: '请先添加收货地址', icon: 'none' }); return }
    request({ url: '/miniapp/orders', method: 'POST', data: Object.assign({ skuId: this.data.skuId, quantity: this.data.quantity }, address) })
      .then(order => wx.redirectTo({ url: '/pages/order/pay?orderNo=' + order.orderNo }))
  }
})
