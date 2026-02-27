export const format = {
  money(value, config) {
    const options = config === "none" ? {} : { style: "currency", minimumFractionDigits: 0, currency: "CNY", ...config }
    return isNaN(value) ? value : (+value).toLocaleString("zh-CN", options)
  },
  bigNumberTransform(value) {
    if (value <= 0 || !value) return "0"

    const units = ["", "万", "亿", "千亿", "万亿", "千万亿"]
    const unitValues = [1, 1e4, 1e8, 1e12, 1e16, 1e20]
    const logValue = Math.log10(value)
    const unitIndex = Math.floor(logValue / 4)

    if (unitIndex < 0) {
      return value.toString()
    }

    const unit = units[unitIndex]
    const unitValue = unitValues[unitIndex]
    const newValue = value / unitValue

    return newValue % 1 === 0 ? `${newValue}${unit}` : `${newValue.toFixed(2)}${unit}`
  },
  limitMaxNum(num, defaultNum = 99, unit = "+") {
    if (!num) return num
    if (Number.isNaN(+num)) return num
    if (num > defaultNum) return `${defaultNum}${unit}`
    return num
  },
  limitInput(value, len = 0) {
    let str = "" + value
    if (str !== "" && /^0*$/.test(str)) return "0"
    str = str
      .replace(/[^0-9.]/g, "")
      .replace(/^0+/, "")
      .replace(/^\./, "0.")
    const parts = str.split(".")
    if (parts.length > 1) {
      if (!len) str = parts[0]
      else if (parts[1].length > len) str = parts[0] + "." + parts[1].substring(0, len)
      else str = parts[0] + "." + parts[1]
    }
    return str || ""
  }
}
