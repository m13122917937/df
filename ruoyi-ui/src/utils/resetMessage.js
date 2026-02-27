import { Message } from "element-ui"
const instances = []
// 默认1.5s 显示关闭按钮
const defaultOptions = {
  duration: 1500,
  showClose: true
}
const newMessage = options => {
  if (instances.length <= 2) {
    instances.push(Message(options))
  } else {
    instances[0].close()
    instances.shift()
    instances.push(Message(options))
  }
}
const messageList = ["error", "success", "info", "warning"]
messageList.forEach(type => {
  newMessage[type] = options => {
    if (typeof options === "string") {
      options = {
        message: options
      }
    }
    options.type = type
    options = Object.assign({}, defaultOptions, options)
    return newMessage(options)
  }
})
export const resetMsg = newMessage
