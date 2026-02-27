import { Notification } from 'element-ui'

const DEFAULT_OPTIONS = {
  title: '',
  message: '',
  type: 'info',
  duration: 4500,
  showClose: true,
  dangerouslyUseHTMLString: false,
}

function mergeOptions(opts) {
  if (typeof opts === 'string') {
    return { ...DEFAULT_OPTIONS, message: opts }
  }
  return { ...DEFAULT_OPTIONS, ...opts }
}

function notify(options = {}) {
  const opts = mergeOptions(options)
  Notification(opts)
}

const GlobalNotification = {
  install(Vue) {
    const api = {
      notify(opts) {
        notify(opts)
      },
      success(message, titleOrOpts) {
        const options = typeof titleOrOpts === 'object' ? { ...titleOrOpts } : { title: titleOrOpts || '' }
        notify({ ...options, message, type: 'success' })
      },
      info(message, titleOrOpts) {
        const options = typeof titleOrOpts === 'object' ? { ...titleOrOpts } : { title: titleOrOpts || '' }
        notify({ ...options, message, type: 'info' })
      },
      warning(message, titleOrOpts) {
        const options = typeof titleOrOpts === 'object' ? { ...titleOrOpts } : { title: titleOrOpts || '' }
        notify({ ...options, message, type: 'warning' })
      },
      error(message, titleOrOpts) {
        const options = typeof titleOrOpts === 'object' ? { ...titleOrOpts } : { title: titleOrOpts || '' }
        notify({ ...options, message, type: 'error' })
      },
      // expose raw element Notification for advanced usage
      raw(options) {
        notify(options)
      },
    }

    // attach to Vue prototype
    Vue.prototype.$notifyCustom = api
  },
}

export default GlobalNotification


