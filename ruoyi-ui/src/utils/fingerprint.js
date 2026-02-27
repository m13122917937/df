import md5 from "js-md5"
let canvasFingerprint = ""
export function getCanvasFingerprint(len = 32) {
  if (canvasFingerprint) return canvasFingerprint.substring(0, len)
  let canvas = document.createElement("canvas")
  let context = canvas.getContext("2d")
  context.font = "18pt Arial"
  context.textBaseline = "top"
  context.fillText("Hello, Fingerprint.", 2, 2)
  canvasFingerprint = md5(canvas.toDataURL("image/jpeg"))
  return canvasFingerprint.substring(0, len)
}

