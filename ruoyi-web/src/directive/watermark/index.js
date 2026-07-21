import watermark from './watermark'

const install = function(Vue) {
  Vue.directive('watermark', watermark)
}

if (window.Vue) {
  window['watermark'] = watermark
  Vue.use(install); // eslint-disable-line
}

watermark.install = install
export default watermark
