package com.ruoyi.sn.model;

import lombok.Data;

// Xiaomi
@Data
public class XiaomiResult extends WarrantyResult {
    private String country;
    private String manufacture;
    private Integer skuId;

    private  String storage;

    private  String color;

    private boolean locked;

    {
        this.brand = "小米";
    }
}