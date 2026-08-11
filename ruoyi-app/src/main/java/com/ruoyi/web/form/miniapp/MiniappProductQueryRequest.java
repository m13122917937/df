package com.ruoyi.web.form.miniapp;

import lombok.Data;

/**
 * 小程序商品查询请求。
 */
@Data
public class MiniappProductQueryRequest {
    private Long categoryId;
    private String productName;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
