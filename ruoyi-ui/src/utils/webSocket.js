// import { commonWebSocketInstance, getSendSocketType } from '@/views/supplier/methods/socket'
import router from "@/router"
// import { getToken } from '@/utils/authToken' // 与后端的协商，websocket请求需要带上token参数
let websock = {}
let repeatPingTime = 1000 * 50 // 心跳时间
let reconnectTime = 1000 // 重连时间

/**
 * 登录 LOGIN(1),
 * 支付结果通知 PAY_RESULT(2),
 * 一件代发列表 DROP_SHIPPING(3),
 * 批量采购列表（暂时废弃） COMPETING_ORDER(4),
 * 当日抢单列表 TODAY_COMPETE_ORDER(5),
 * 再次抢单列表 ROBBED_CONFIRM_ORDER(6),
 * 接龙抢单列表 ORDER_MARKETING(7),

 * 采购方订单列表 DEMAND_ORDER_STATUS(8),
 * 询价采购报价中列表 INQUIRY_HANG_ORDER(9)
 */
// 推送类型 对应 路由 name
export const wsType = {
  1: "login",
  2: "myWallet",
  3: "agent",
  5: "toDayGrab",
  6: "againGrab",
  7: "pickDragonGrabOrder",
  9: "inTheQuotationCount",
  10: "newPurchase",
  11: "HeadSpeedTable",
  12: "NewbidList",
  14: "InBidding"
}

export function getSendSocketType() {
  let name = router.app.$route.name
  let type = -1
  for (let key in wsType) {
    if (wsType[key] === name) type = key
  }
  return type
}

// 发送消息回调
function messageCallBack(instance) {
  if (instance.pingTimer) window.clearTimeout(instance.pingTimer)
  instance.pingTimer = window.setTimeout(() => {
    websocketSendMsg(instance, "ping")
  }, repeatPingTime)
}

// 重连
function reconnect(instance, params) {
  if (instance.handleClose) return
  if (instance.reconnectTimer) return
  if (instance.reconnectNum > 300) return closeWebsocket(instance)
  instance.reconnectTimer = window.setTimeout(() => {
    sendWebsocket(...params)
  }, reconnectTime)
}

export function sendWebsocket(url, successCallback, errCallback, openCallback, closeCallback) {
  let params = [url, successCallback, errCallback, openCallback, closeCallback]
  if (typeof WebSocket === "undefined") {
    console.log("您的浏览器不支持WebSocket，无法获取数据")
    return false
  }

  let msg = "ping"
  let link = url
  if (typeof url === "object") {
    link = url.url
    msg = getSendSocketType()
  }
  // ws请求完整地址
  let requstWsUrl = process.env.VUE_APP_SERVER_PUSH + link

  let webSock = new WebSocket(requstWsUrl)

  webSock.onopen = function () {
    if (msg !== "ping") websocketSendMsg(webSock, msg)
    webSock.reconnectNum = 100
    if (openCallback) {
      openCallback()
    }
  }

  webSock.onmessage = function (e) {
    messageCallBack(webSock)
    if (msg !== "ping") websocketSendMsg(webSock, msg)
    successCallback(JSON.parse(e.data))
  }

  webSock.sendMsg = (obj, callback) => {
    webSock.send(obj)
    messageCallBack(webSock)
    if (callback) callback()
  }

  webSock.onerror = function () {
    reconnect(webSock, params)
    if (errCallback) {
      errCallback()
    }
    // errCallback()
  }
  webSock.onclose = function (e) {
    reconnect(webSock, params)
    window.clearTimeout(webSock.pingTimer)
    webSock.pingTimer = null
    if (websock[url]) delete websock[url]
    if (closeCallback) {
      closeCallback(e)
    }
  }
  webSock.repeatCount = 0
  websock[url] = webSock
  return websock[url]
}

/**
 * 发起websocket请求函数
 * @param {string} url ws连接地址
 * @param {Object} agentData 传给后台的参数
 * @param {function} successCallback 接收到ws数据，对数据进行处理的回调函数
 * @param {function} errCallback ws连接错误的回调函数
 */
// export function sendWebsocket (url, agentData, successCallback, errCallback, openCallback) {
//   wsUrl = url;
//   initWebSocket()
//   messageCallback = successCallback
//   errorCallback = errCallback
//   openWebsock = openCallback || function(){}
//   websocketSend(agentData)
// }

/**
 * 关闭websocket函数
 * config 入参
 * ① undefined  关闭所有 websocket
 * ② url 或 websocket 实例 关闭传入的 websocket (或url对应websocket)
 * ③ url 数组 或 websocket 实例数组 关闭数组中所有websocket (或url对应websocket)
 */
export function closeWebsocket(config) {
  let instanceArr = config ? (Array.isArray(config) ? config : [config]) : Object.values(websock)
  for (let i = 0; i < instanceArr.length; i++) {
    let instance = instanceArr[i]
    if (typeof instance === "string") instance = websock[instance]
    if (instance) {
      instance.handleClose = true // 手动关闭 不自动重连
      if (instance.reconnectTimer) window.clearTimeout(instance.reconnectTimer) // 清除延时重连
      instance.close() // 关闭websocket
      // instance.onclose() // 关闭websocket
    }
  }
}

export function websocketSendMsg(instance, msg, repeat) {
  if (typeof instance === "string") instance = websock[instance]
  if (typeof instance !== "object") return
  msg = typeof msg === "string" ? msg : JSON.stringify(msg)
  if (instance.currentType === msg && msg !== "ping") return
  if (repeat) instance.repeatCount++
  else instance.repeatCount = 0

  if (instance.repeatCount > 100) {
    instance.repeatCount = 0
    return
  }
  if (instance.readyState === instance.OPEN) {
    instance.sendMsg(msg)
    instance.currentType = msg
    instance.repeatCount = 0
  } else {
    setTimeout(() => {
      websocketSendMsg(instance, msg, true)
    }, 300)
  }
}
