const { request } = require('../../utils/request')

Page({
  data: { product: null, selectedSku: null, quantity: 1 },
  onLoad({ id }) { request({ url: '/miniapp/products/' + id }).then(product => this.setData({ product, selectedSku: product.skus && product.skus[0] })) },
  selectSku(event) { this.setData({ selectedSku: event.currentTarget.dataset.sku }) },
  buyNow() {
    if (!this.data.selectedSku) return
    wx.navigateTo({ url: `/pages/order/create?skuId=${this.data.selectedSku.id}&quantity=${this.data.quantity}` })
  }
})
