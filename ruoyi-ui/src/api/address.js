import request from "@/utils/request"

/**
 * @description: 获取省列表
 * @param {Object} params - 请求参数，可包含status等
 * */
export function getProvinces(params = {}) {
  return request({
    url: "/order/new/province/count",
    method: "get",
    params
  });
}

/**
 * @description: 获取市列表
 * @param { number } pid 省id
 * */
export function getCities(pid) {
  return request({
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
  return request({
    url: "/area/area",
    method: "get",
    params: { pid }
  })
}

//获取省列表
export function getProvinceList() {
  return request({
    url: "area/province",
    method: "get"
  })
}

// 获取多个市 /system/area/city/list
export function apiPostMustCityList(data) {
  return request({
    url: "/area/city/list",
    method: "post",
    data
  })
}

// 获取地区接口
export function apiAreaTree(params) {
  return request({
    url: `/area/tree`,
    method: `GET`,
    params
  })
}
