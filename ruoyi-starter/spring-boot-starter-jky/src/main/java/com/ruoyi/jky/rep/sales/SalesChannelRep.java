package com.ruoyi.jky.rep.sales;

import lombok.Data;

/**
 * 吉客云销售渠道信息。
 */
@Data
public class SalesChannelRep {

    private Long channelId;
    private String channelCode;
    private String channelName;
    private Integer channelType;
    private String onlinePlatTypeCode;
    private String onlinePlatTypeName;
    private Long channelDepartId;
    private String channelDepartName;
    private String linkMan;
    private String linkTel;
    private String officeAddress;
    private Long groupId;
    private String email;
    private Long companyId;
    private String companyName;
    private String postcode;
    private Long countryId;
    private String countryName;
    private Long provinceId;
    private String provinceName;
    private Long cityId;
    private String cityName;
    private Long townId;
    private String townName;
    private Long streetId;
    private String streetName;
    private String memo;
    private String warehouseCode;
    private String warehouseName;
    private Integer chargeType;
    private Long cateId;
    private String cateName;
}
