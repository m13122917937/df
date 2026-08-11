package com.ruoyi.web.vo.miniapp;

import lombok.Data;

/** 小程序收货地址响应。 */
@Data
public class MiniappAddressVO {
    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
    private Integer defaultAddress;
}
