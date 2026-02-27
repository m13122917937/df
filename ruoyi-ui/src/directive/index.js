import hasRole from "./permission/hasRole"
import hasPermi from "./permission/hasPermi"
import dialogDrag from "./dialog/drag"
import dialogDragWidth from "./dialog/dragWidth"
import dialogDragHeight from "./dialog/dragHeight"
import clipboard from "./module/clipboard"
import tableHeight from "./tableHeight/tableHeight"
import layoutTableHeight from "./tableHeight/layoutTableHeight"
import tableScrollToBottom from "./tableHeight/scrollToBottom"
import loadMore from "./loadMore/index"
import input from "./input/input"
import scroll from "./scroll"
import watermark from "./module/watermark"
import tooltipAutoShow from "./tooltipAutoShow"
import selectLoad from "./selectLoad/index.js"
import clickOutside from "./clickOutside"
import { tableCustomHeight } from "./tableHeight/tableCustomHeight"
import height from "./tableHeight/height"
import throttleClick from "./throttleClick"
const install = function (Vue) {
  Vue.directive("hasRole", hasRole)
  Vue.directive("hasPermi", hasPermi)
  Vue.directive("clipboard", clipboard)
  Vue.directive("dialogDrag", dialogDrag)
  Vue.directive("dialogDragWidth", dialogDragWidth)
  Vue.directive("dialogDragHeight", dialogDragHeight)
  Vue.directive("tableHeight", tableHeight)
  Vue.directive("layoutTableHeight", layoutTableHeight)
  Vue.directive("tableScroll", tableScrollToBottom)
  Vue.directive("loadMore", loadMore)
  Vue.directive("input", input)
  Vue.directive("watermark", watermark)
  Vue.directive("scroll", scroll)
  Vue.directive("tooltipAutoShow", tooltipAutoShow)
  Vue.directive("selectLoad", selectLoad)
  Vue.directive("clickOutside", clickOutside)
  Vue.directive("tableCustomHeight", tableCustomHeight)
  Vue.directive("height", height)
  Vue.directive("throttleClick", throttleClick)
}

if (window.Vue) {
  window["hasRole"] = hasRole
  window["hasPermi"] = hasPermi
  Vue.use(install) // eslint-disable-line
}

export default install
