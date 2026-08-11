function request(options) {
  const app = getApp()
  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.apiBaseUrl + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: app.globalData.token ? { Authorization: 'Bearer ' + app.globalData.token } : {},
      success: ({ data }) => data.code === 200 ? resolve(data.data) : reject(new Error(data.msg || '请求失败')),
      fail: reject
    })
  })
}

module.exports = { request }
