import { serverRequest } from "@/api/system"

/**
 * @param { number } exportType
 * 用户最近3条导出结果
 * */
export function getDownFileList(exportType) {
  return serverRequest({
    url: `/export/list/${exportType}`,
    method: "get"
  })
}

// 获取list
export function sklist() {
  return serverRequest({
    url: "/export/importResults",
    method: "get"
  })
}

export function postPageList(data, params) {
  return serverRequest({
    url: "/export/pageList",
    method: "post",
    params,
    data
  })
}
