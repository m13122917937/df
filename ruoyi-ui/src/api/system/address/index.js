import { serverRequest } from "@/api/system"

/**
 * @description: 获取省列表
 * */
export function getProvinces() {
  return serverRequest({
    url: "area/province",
    method: "get"
  })
}

/**
 * @description: 获取市列表
 * @param { number } pid 省id
 * */
export function getCities(pid) {
  return serverRequest({
    url: "/area/city",
    method: "get",
    params: { pid }
  })
}

/**
 * @description: 获取区列表
 * @param { number } pid 市id
 * */
export function getAreas(pid) {
  return serverRequest({
    url: "/area/area",
    method: "get",
    params: { pid }
  })
}

//获取省列表
export function getProvinceList() {
  return serverRequest({
    url: "area/province",
    method: "get"
  })
}

// 获取多个市 /system/area/city/list
export function apiPostMustCityList(data) {
  return serverRequest({
    url: "/area/city/list",
    method: "post",
    data
  })
}

// 获取地区接口
export function apiAreaTree(params) {
  return serverRequest({
    url: `/area/tree`,
    method: `GET`,
    params
  })
}
