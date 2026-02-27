import { serverRequest } from "@/api/system"

//发送验证码
export function getMobileCode(mobile, type) {
  return serverRequest({
    url: `/sms/code/${mobile}/${type}`,
    method: "get"
  })
}

// 验证短信验证码
export function validateCaptcha(params) {
  return serverRequest({
    url: `/sms/validateCaptcha`,
    method: "get",
    params
  })
}
