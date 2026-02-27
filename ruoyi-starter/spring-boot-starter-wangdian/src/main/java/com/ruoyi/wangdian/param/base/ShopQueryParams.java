package com.ruoyi.wangdian.param.base;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺查询参数实体类
 * 用于封装店铺查询请求中的参数
 */
@Data
@Builder
public class ShopQueryParams implements Serializable {

    private static final long serialVersionUID = 1L;

    // 店铺编号 - 可选
    private String shopNo;

    // 平台ID - 可选
    private Integer platformId;

    // 平台ID（批量）- 可选，多个平台ID用逗号分隔
    private String platformIds;

    // 分页大小 - 可选，默认30，范围1~100
    private Integer pageSize;

    // 页号 - 可选，默认从0开始
    private Integer pageNo;

    // 是否停用：0未停用，1停用；不传默认为0
    private String isDisabled;

}