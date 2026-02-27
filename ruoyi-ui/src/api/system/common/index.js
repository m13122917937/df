import { serverRequest } from "@/api/system"
import { getCancelToken } from "@/utils/request"

// 获取服务器当前时间
export async function getNowTime() {
  return serverRequest({
    url: "/common/time",
    method: "get"
  })
}

//获取唯一id
export function getOnlyId(hasToken = true) {
  const headers = hasToken ? {} : { isToken: false }
  return serverRequest({
    url: "/common/sse/id",
    method: "get",
    headers: {
      ...headers
    }
  })
}

// 文件上传
export function upload(data, cancelFn = null) {
  return serverRequest({
    url: "/common/upload",
    method: "post",
    data,
    cancelToken: getCancelToken(cancelFn)
  })
}
// 多文件上传
export function uploads(data, cancelFn = null) {
  return serverRequest({
    url: "/common/uploads",
    method: "post",
    data,
    cancelToken: getCancelToken(cancelFn)
  })
}
