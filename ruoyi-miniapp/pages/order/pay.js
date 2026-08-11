const { request } = require('../../utils/request')

Page({
  data: { orderNo: '' },
  onLoad({ orderNo }) { this.setData({ orderNo }) },
  pay() {
    request({ url: `/miniapp/payments/${this.data.orderNo}/prepay`, method: 'POST' }).then(params => wx.requestPayment({
      timeStamp: params.timeStamp, nonceStr: params.nonceStr, package: params.package, signType: params.signType, paySign: params.paySign,
      success: () => wx.showToast({ title: '支付结果处理中' })
    }))
  }
})
