import request from "@/utils/request";

/**
 * @description: 合同分页列表
 */
export function pageContractApi(data) {
  const { pageNum, pageSize, ...filters } = data || {};
  return request({
    url: "/contract/page/list",
    method: "POST",
    params: { pageNum, pageSize },
    data: filters,
  });
}

/**
 * @description: 合同详情
 */
export function getContractApi(id) {
  return request({
    url: `/contract/${id}`,
    method: "GET",
  });
}

/**
 * @description: 新增草稿合同
 */
export function saveContractApi(data) {
  return request({
    url: "/contract/save",
    method: "POST",
    data,
  });
}

/**
 * @description: 修改草稿合同
 */
export function updateContractApi(data) {
  return request({
    url: "/contract/update",
    method: "POST",
    data,
  });
}

/**
 * @description: 删除草稿合同
 */
export function removeContractApi(id) {
  return request({
    url: `/contract/${id}`,
    method: "DELETE",
  });
}

/**
 * @description: 发起签署
 */
export function launchContractApi(data) {
  return request({
    url: "/contract/launch",
    method: "POST",
    data,
  });
}

/**
 * @description: 撤销签署
 */
export function cancelContractApi(id) {
  return request({
    url: `/contract/${id}/cancel`,
    method: "PUT",
  });
}

/**
 * @description: 获取签署URL
 */
export function getSignUrlApi(id, signerType = "our") {
  return request({
    url: `/contract/${id}/sign-url`,
    method: "GET",
    params: { signerType },
  });
}

/**
 * @description: 手动同步状态
 */
export function syncContractStatusApi(id) {
  return request({
    url: `/contract/${id}/sync`,
    method: "PUT",
  });
}

/**
 * @description: 上传合同文件到 e 签宝（返回 fileId）
 */
export function uploadContractFileApi(formData) {
  return request({
    url: "/contract/upload-file",
    method: "POST",
    headers: { "Content-Type": "multipart/form-data" },
    data: formData,
  });
}

/**
 * @description: 我方主体下拉
 */
export function listPayerApi(payName) {
  return request({
    url: "/bill/payer/list",
    method: "GET",
    params: { payName },
  });
}

/**
 * @description: 供应商搜索（复用现有 company list）
 */
export function listCompanyApi(data) {
  return request({
    url: "/company/list",
    method: "POST",
    data,
  });
}
