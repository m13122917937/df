/**
 * 公共接口请求节流
 * */
export async function requestThrottle(arr, fun) {
  let type = typeof arr
  if (type !== "object" || arr === null) return
  let symKey = Object.getOwnPropertySymbols(arr).find(o => o.toString() === "Symbol(loading)")
  if (!symKey) symKey = Symbol("loading")

  let len = Array.isArray(arr) ? arr.length : Object.keys(arr).length
  if (!len && !arr[symKey]) {
    arr[symKey] = true
    try {
      await fun()
    } finally {
      if (arr[symKey]) delete arr[symKey]
    }
  }
}
