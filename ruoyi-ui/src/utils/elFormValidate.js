import { validPhone } from "@/utils/validate"

export const phoneValidatePass = (rule, value, callback) => {
  if (!validPhone(value)) {
    callback(new Error("请输入正确的11位手机号"))
  }
  if (!value) {
    callback(new Error("手机号不能为空"))
  }
  callback()
}
