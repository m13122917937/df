const { request } = require('../../utils/request')

Page({
  data: { form: { receiverName: '', receiverPhone: '', provinceName: '', cityName: '', districtName: '', detailAddress: '', defaultAddress: true } },
  onLoad(options) {
    if (!options.id) return
    request({ url: '/miniapp/addresses' }).then(addresses => {
      const form = (addresses || []).find(item => item.id === Number(options.id))
      if (form) this.setData({ form })
    })
  },
  onInput(event) { this.setData({ [`form.${event.currentTarget.dataset.field}`]: event.detail.value }) },
  onDefaultChange(event) { this.setData({ 'form.defaultAddress': event.detail.value }) },
  save() {
    const form = this.data.form
    if (!form.receiverName || !form.receiverPhone || !form.provinceName || !form.cityName || !form.districtName || !form.detailAddress) {
      wx.showToast({ title: '请完整填写收货地址', icon: 'none' }); return
    }
    const method = form.id ? 'PUT' : 'POST'
    const url = form.id ? '/miniapp/addresses/' + form.id : '/miniapp/addresses'
    request({ url, method, data: form }).then(() => { wx.showToast({ title: '保存成功' }); wx.navigateBack() })
  }
})
