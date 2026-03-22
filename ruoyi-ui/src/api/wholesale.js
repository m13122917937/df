import request from "@/utils/request";

//公用接口 --------------------------------------start
/**
 * @description: 品牌订单数量
 * @param {*} params
 * @returns
 */
export function getBrandCountApi(params) {
  return request({
    url: `/order/new/brand/count`,
    method: "get",
    params,
  });
}

  /**
   * @description: 省市订单数量
   * @param {*} params
   * @returns
   */
  export function getProvinceCountApi(params) {
    return request({
      url: `/order/new/province/count`,
      method: "get",
      params,
    });
  }


/**
 * @description: 退货追单
 * @param {*} data
 * @returns
 */
export function revokeOrderApi(data) {
  return request({
    url: `/order/chase/revoke`,
    method: "post",
    data,
  });
}



/**
 * @description: 查询品类列表
 * @param {*} data
 * @returns
 */
export function getCategoryListApi(data) {
  return request({
    url: `/order/filter/category`,
    method: "post",
    data,
  });
}

/**
 * @description: 查询店铺名称列表
 * @param {*} data
 * @returns
 */
export function getShopNameListApi(data) {
  return request({
    url: `/order/filter/shopName`,
    method: "post",
    data,
  });
}

/**
 * @description: 查询快递
 * @param {*} params
 * @returns
 */
export function getExpressInfoApi(params) {
  return request({
    url: `/system/company/info/${params.logisticsNo}`,
    method: "get",
  });
}

/**
 * @description: 查询省份列表
 * @param {*} params
 * @returns
 */
export function getProvinceListApi(params) {
  return request({
    url: `/system/province/list`,
    method: "get",
    params,
  });
}

/**
 * @description: 获取城市列表
 * @param {*} params 省份id:provinceId
 * @returns
 */
export function getCityListApi(provinceId) {
  return request({
    url: "/system/city/" + provinceId,
    method: "GET",
  });
}

/**
 * @description: 获取tab数据
 * @param {*} data
 * @returns
 */
export function getCountHeaderApi(data) {
  return request({
    url: `/order/filter/countHeader`,
    method: "post",
    data,
  });
}

//公用接口 ----------------------------------end














//transitOrders ----------------在途订单--start
/**
 * @description: 获取在途订单列表
 * @param {*} params
 * @param {number} params.pageNum 当前页码
 * @param {number} params.pageSize 每页条数
 * @param {number} params.status 订单状态：6-在途
 * @param {string} params.brand 品牌
 * @param {string} params.provinceId 省份
 * @param {string} params.cityId 城市
 * @param {string} params.erpOrderId 商家单号
 * @returns
 *
 */
export function getTransitListApi(params) {
  return request({
    url: `/order/new/list/transit`,
    method: "get",
    params,
  });
}

/**
 * @description: 订单转异常
 * @param {*} params
 * @returns
 */
export function getErrorApi(params) {
  return request({
    url: `/order/error/error`,
    method: "get",
    params,
  });
}
//transitOrders -------------在途订单--end








/**
 * @description: 获取追单订单列表
 * @param {*} params
 * @returns
 */
export function getChaseListApi(params) {
  return request({
    url: `/order/new/list/chase`,
    method: "get",
    params,
  });
}













// abnormalOrders;----异常--------
/**
 * @description: 获取异常订单列表
 * @param {*} params
 * @returns
 */
export function getErrorListApi(params) {
  return request({
    url: `/order/error/list`,
    method: "get",
    params,
  });
}

/**
 * @description: 异常订单转以收货
 * @param {*} data
 * @returns
 */
export function turnToEndingApi(params) {
  return request({
    url: `/order/error/error2ending`,
    method: "get",
    params,
  });
}

/**
 * @description: 添加异常订单
 * @param {*} params
 * @returns
 */
export function addErrorApi(params) {
  return request({
    url: `/order/error/error`,
    method: "get",
    params,
  });
}

// abnormalOrders;------------








//returnOrders------退货追单--start
/**
 * @description: 添加退货追单
 * @param {*} params
 * @returns
 */
export function getReturnOrderApi(params) {
  return request({
    url: `/order/chase/list/chase`,
    method: "get",
    params,
  });
}

 /**
  * @description: 订单转正常
 * @param {*} params
 * @returns
 */
export function changeNormalApi(params) {
  return request({
    url: `/order/chase/chase2normal`,
    method: "get",
    params,
  });
}


//returnOrders--------end







//confirmedOrders ------确认完成订单--start
/**
 * @description: 获取确认订单列表
 * @param {*} params
 * @returns
 */
export function getConfirmedListApi(params) {
  return request({
    url: `/order/new/list/ending/history`,
    method: "get",
    params,
  });
}


/**
 * @description: 确认收货接口
 * @param {*} params
 * @returns
 */
export function confirmEndingOrderApi(params) {
  return request({
    url: `/order/new/ending/confirm`,
    method: "get",
    params,
  });
}


//confirmedOrders ------确认完成订单--end





//afterOrder------售后订单--start
/**
 * @description: 获取售后列表
 * @param {*} params
 * @returns
 */
export function getAfterSalesListApi(params) {
  return request({
    url: `/order/new/list/afterSales`,
    method: "get",
    params,
  });
}
//afterOrder------end




// cancelledOrders ------撤销订单--start
/**
 * @description: 获取撤销退货追单列表
 * @param {*} params
 * @returns
 */
export function getCancelledListApi(params) {
  return request({
    url: `/order/chase/list/revoke`,
    method: "get",
    params,
  });
}

//cancelledOrders ------end





/**
 * @description: 获取order的list数据
 * @param {*} data
 * @returns
 */
export function getOrderListApi(pageData, data) {
  return request({
    url: `/order/new/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "post",
    data,
  });
}

/**
 * @description: 获取order的send的list数据
 * @param {*} data
 * @returns
 */
export function getOrderSendListApi(pageData, data) {
  return request({
    url: `/order/new/send/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "post",
    data,
  });
}



/**
 * @description: 导出订单列表
 * @param {*} pageData 分页参数
 * @param {*} data 查询条件
 * @returns
 */
export function exportOrderListApi(pageData, data) {
  return request({
    url: `/order/new/list/export?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
    method: "post",
    data,
    responseType: "blob",
  });
}

export function getAllOrderListApi(pageData,data) {
    return request({
      url: `/order/all/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
      method: "post",
      data,
    });
  }
  // 导出 全部订单列表 - 直接下载（兼容旧版）
  export function getAllOrderExportApi(data) {
    return request({
      url: `/order/all/export`,
      method: "post",
      data,
      responseType: "blob",
    });
  }

  // 第一步：生成Excel文件到服务器
  export function generateOrderExcelApi(data) {
    return request({
      url: `/system/excel/generate`,
      method: "post",
      data,
    });
  }

  // 第二步：下载已生成的Excel文件
  export function downloadOrderExcelApi(fileId) {
    return request({
      url: `/system/excel/download/${fileId}`,
      method: "get",
      responseType: "blob",
    });
  }

  // 查询Excel生成任务状态
  export function getExcelTaskStatusApi(fileId) {
    return request({
      url: `/system/excel/status/${fileId}`,
      method: "get",
    });
  }

  // 获取当前用户最近的Excel导出任务列表（最近5条）
  export function getRecentExcelTasksApi() {
    return request({
      url: `/system/excel/recent`,
      method: "get",
    });
  }

  export function getSkuListApi(data,pageData) {
    return request({
      url: `/product/sku/page/list?pageNum=${pageData.pageNum}&pageSize=${pageData.pageSize}`,
      method: "post",
      data,
    });
  }
  // 获取品牌列表
  export function getBrandListApi(data) {
    return request({
      url: `/product/brand`,
      method: "get",
      data,
    });
  }

  // 根据品牌获取类别列表
  export function getProductCategoryListApi(data) {
    return request({
      url: `/product/category`,
      method: "get",
      data,
    });
  }

  // 新增商品
  export function createProductSkuApi(data) {
    return request({
      url: `/product/sku`,
      method: "post",
      data,
    });
  }


  

