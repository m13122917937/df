import { resetMsg } from "@/utils/resetMessage"

export function mockRequest(config = { result: [true, { code: 200 }, "操作成功"], time: 2000 }, ...args) {
  for (const argItem of args) {
    console.log(argItem)
  }

  return new Promise(resolve => {
    let timer = setTimeout(() => {
      let [suc, msg] = config.result || [true, { code: 200 }]
      if (!suc) {
        let message = msg || "服务端异常"
        resetMsg({
          message: "<pre>" + message + "<pre>",
          type: "error",
          // offset:100,
          dangerouslyUseHTMLString: true,
          duration: 5 * 1000,
          customClass: "message-moreline"
        })
      }

      resolve(config?.result)
      clearTimeout(timer)
    }, config?.time || 2000)
  })
}

// eslint-disable-next-line no-unused-vars
export function getRandomInt(minOrArray, max) {
  if (Array.isArray(minOrArray)) {
    // 如果传入的是数组，则从数组中随机选择一个元素
    const array = minOrArray
    const randomIndex = Math.floor(Math.random() * array.length)
    return array[randomIndex]
  } else {
    // 如果传入的是最小值和最大值，则生成一个在这个范围内的随机数
    const min = Math.min(minOrArray, max)
    const max = Math.max(minOrArray, max)
    return Math.random() * (max - min) + min
  }
}
