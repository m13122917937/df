import request from "@/utils/request";
/**
 * @description: 商品映射列表
 * @param {*} data
 * @returns
 */
export function getRelListApi(pageData, queryData) {
  return request({
    url: `/apply/imei/rel?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "post",
    data: queryData,
  });
}

/**
 * @description: 同意
 * @param {*} data
 * @returns
 */
export function agreeApi(id) {
  return request({
    url: `/apply/agree/${id}`,
    method: "post",
  });
}

/**
 * @description: 删除
 * @param {*} data
 * @returns
 */
export function delApi(id) {
  return request({
    url: `/apply/del/${id}`,
    method: "post",
  });
}

/**
 * @description: 拒绝
 * @param {*} data
 * @returns
 */
export function refuseApi(id) {
  return request({
    url: `/apply/refuse/${id}`,
    method: "post",
  });
}