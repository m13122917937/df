const { request } = require('./utils/request')
const apiBaseUrl = wx.getStorageSync('miniappApiBaseUrl') || ''

App({
  globalData: { token: '', apiBaseUrl },
  onLaunch() {
    this.silentLogin()
  },
  silentLogin() {
    if (!this.globalData.apiBaseUrl) {
      wx.showModal({ title: '未配置服务地址', content: '请在发布前配置 ruoyi-app 的 HTTPS 合法域名。', showCancel: false })
      return
    }
    wx.login({
      success: ({ code }) => request({ url: '/miniapp/auth/silent-login', method: 'POST', data: { code } })
        .then(({ token }) => { this.globalData.token = token || '' })
    })
  }
})
