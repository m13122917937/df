const { request } = require('../../utils/request')

Page({
  data: { addresses: [], selecting: false },
  onLoad(options) { this.setData({ selecting: options.select === '1' }) },
  onShow() { this.load() },
  load() { request({ url: '/miniapp/addresses' }).then(addresses => this.setData({ addresses: addresses || [] })) },
  add() { wx.navigateTo({ url: '/pages/address/edit' }) },
  edit(event) { wx.navigateTo({ url: '/pages/address/edit?id=' + event.currentTarget.dataset.id }) },
  select(event) {
    if (!this.data.selecting) return
    const address = this.data.addresses.find(item => item.id === event.currentTarget.dataset.id)
    wx.setStorageSync('miniappSelectedAddress', address)
    wx.navigateBack()
  },
  remove(event) {
    const id = event.currentTarget.dataset.id
    wx.showModal({ title: '删除地址', content: '确认删除该收货地址吗？', success: result => {
      if (result.confirm) request({ url: '/miniapp/addresses/' + id, method: 'DELETE' }).then(() => this.load())
    } })
  }
})
