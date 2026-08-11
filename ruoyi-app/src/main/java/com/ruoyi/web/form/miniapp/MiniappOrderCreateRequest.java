package com.ruoyi.web.form.miniapp;

import lombok.Data;

/**
 * 小程序创建订单请求。
 */
@Data
public class MiniappOrderCreateRequest {
    private Long skuId;
    private Integer quantity;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
}
