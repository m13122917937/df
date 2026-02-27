import Vue from "vue"
import moment from "moment"
import { debounce } from "@/utils"
const watermarkCreate = (el, binding) => {
  const date = moment().format("YYYY/MM/DD")
  // binding.value结构为 { text: string | string[], font: string, textColor: string, zIndex: number }
  // text: 水印文字  font: 字体  textColor: 文字颜色 倍数
  let { text, font, textColor, zIndex, widthMultiple = 4, height = 54, showDate = true } = binding.value
  text = `${text} ${showDate ? date : ""}` || ""
  // 创建画布
  let canvas = null
  if (!el.watermarkCanvas) {
    el.watermarkCanvas = document.createElement("canvas")
  }
  canvas = el.watermarkCanvas
  // 获取画笔
  const ctx = canvas.getContext("2d")
  canvas.style.display = "none"
  if (ctx) {
    // 获取文字宽度
    const offsetX = Math.ceil(ctx.measureText(text).width)
    // 宽度会影响列与列之间的距离
    canvas.setAttribute("width", String(offsetX * widthMultiple))
    // 高度会影响行与行之间的距离
    canvas.setAttribute("height", height + 6)
    // 旋转画笔
    ctx.rotate((-15 * Math.PI) / 180)
    ctx.font = font || "16px PingFang-SC-Regular, PingFang-SC, sans-serif"
    ctx.fillStyle = textColor || "rgba(3,3,3,0.08)"
    ctx.textAlign = "left"
    ctx.textBaseline = "middle"
    // 绘制文本
    ctx.fillText(text, 0, height - 6)
  }
  let container = null
  if (!el.watermarkContainer) {
    el.watermarkContainer = document.createElement("div")
  }
  container = el.watermarkContainer
  container.style.position = "absolute"
  container.style.width = "100%"
  // container.style.height = '100%';
  container.style.zIndex = zIndex || "1"
  container.style.top = "0"
  container.style.left = "0"
  container.style.pointerEvents = "none"
  container.style.backgroundImage = "url(" + canvas.toDataURL("image/png") + ")"
  el.appendChild(container)
  Vue.nextTick(() => {
    let height = el.offsetHeight
    if (height) container.style.height = `${height}px`
    setTimeout(() => {
      if (height != el.offsetHeight) {
        container.style.height = `${el.offsetHeight}px`
      }
    }, 150)
  })
}

export default {
  inserted: function (el, binding) {
    el.resizeListener = debounce(() => {
      watermarkCreate(el, binding)
    }, 16)
    const { value } = binding
    let { firstLoaded } = value || {}
    firstLoaded = firstLoaded ?? true // 不写默认为true
    if (firstLoaded) el.resizeListener()
    window.addEventListener("resize", el.resizeListener, true)
  },
  update: function (el, binding) {
    watermarkCreate(el, binding)
  },
  unbind(el) {
    el.watermarkCanvas = null
    el.watermarkContainer = null
    window.removeEventListener("resize", el.resizeListener, true)
  }
}
