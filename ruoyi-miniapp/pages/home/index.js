const { request } = require('../../utils/request')

Page({
  data: { banners: [], categories: [], products: [], keyword: '' },
  onShow() { this.loadHome() },
  loadHome() {
    request({ url: '/miniapp/home' }).then(home => this.setData({ banners: home.banners || [], categories: home.categories || [], products: home.recommendedProducts || [] }))
  },
  onSearchInput(event) { this.setData({ keyword: event.detail.value }) },
  onSearch() { request({ url: '/miniapp/products', data: { keyword: this.data.keyword } })
    .then(products => this.setData({ products: products.records || products })) },
  openProduct(event) { wx.navigateTo({ url: '/pages/product/detail?id=' + event.currentTarget.dataset.id }) }
})
