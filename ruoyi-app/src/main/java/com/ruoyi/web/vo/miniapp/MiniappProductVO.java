package com.ruoyi.web.vo.miniapp;

import lombok.Data;

/**
 * 小程序商品响应。
 */
@Data
public class MiniappProductVO {
    private Long id;
    private Long categoryId;
    private String productName;
    private String subtitle;
    private String mainImageUrl;
    private Integer recommended;
}
