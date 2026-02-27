import { serverRequest } from "@/api/system"

/**
 * @description 字典项查询接口
 * @param {String} type
 */
export function getDictType(type) {
  return serverRequest({
    url: `/dict/type/${type}`,
    method: "get"
  })
}
