import request from "@/utils/request"
const apiModule = "system"

export function serverRequest(obj) {
  if (!obj.apiModule) obj.apiModule = apiModule
  return request(obj)
}
