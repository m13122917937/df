const timeType = {
  s: 1,
  m: 60,
  h: 60 * 60,
  d: 60 * 60 * 24,
  w: 60 * 60 * 24 * 7,
  M: 60 * 60 * 24 * 30,
  y: 60 * 60 * 24 * 365
}
function timStr(num) {
  return ("0" + num).slice(-2)
}
/**
 * 时间偏差
 * @param {String | number } time 时间戳或时间字符串
 * @param {Object | number | undefined} config 偏差时间或配置 { diff: 偏差时间 默认：0, type: 偏差时间类型 默认："s", day: 日期格式 默认："日"。例："今日"，"明日"，"后日"，"第几日" }
 * */
export function displacementTime(time = Date.now(), config = {}) {
  if (!time) return ""
  if (time.indexOf("-") > -1) time = time.replace(/-/g, "/")
  if (new Date(time).toString() === "Invalid Date") return ""
  let { diff = 0, type = "s", day = "日" } = typeof config === "number" ? { diff: config } : config === null ? {} : config
  diff = diff * (timeType[type] || 1) * 1000
  let resultTime = new Date(new Date(time).getTime() + diff)
  let diffDate = resultTime.getDate() - new Date().getDate()
  let difObj = { 0: "今" + day, 1: (day === "日" ? "次" : "明") + day, 2: "后" + day }
  let resTime = timStr(resultTime.getHours()) + ":" + timStr(resultTime.getMinutes()) + ":" + timStr(resultTime.getSeconds())
  let resDate = diffDate < 0 ? timStr(resultTime.getMonth() + 1) + "-" + timStr(resultTime.getDate()) : difObj[diffDate] || `第${diffDate + 1}${day}`
  return resDate + " " + resTime
}
