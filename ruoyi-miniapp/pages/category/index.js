const { request } = require('../../utils/request')

Page({
  data: { categories: [] },
  onShow() { request({ url: '/miniapp/categories' }).then(categories => this.setData({ categories })) },
  openCategory(event) { wx.navigateTo({ url: '/pages/home/index?categoryId=' + event.currentTarget.dataset.id }) }
})
