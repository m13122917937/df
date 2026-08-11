package com.ruoyi.web.form.miniapp;

import lombok.Data;

/** 小程序收货地址请求。 */
@Data
public class MiniappAddressRequest {
    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
    private Boolean defaultAddress;
}
