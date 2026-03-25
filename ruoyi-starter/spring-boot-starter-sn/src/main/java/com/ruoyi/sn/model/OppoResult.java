package com.ruoyi.sn.model;

import lombok.Data;

// Huawei
@Data
public class OppoResult extends WarrantyResult {
    private String img;
    private String skuItemCode;
    private String product;
    private String productOffering;
    private String productCategoryCode;
    private TypeDetail type;
    private boolean hasCare;
    private PurchaseDetail purchase;
    private String warrStatus;
    private String covered;
    private Integer daysleft;
    private String color;
    private String storage;
    private String productDate;
    private Right[] right;

    {
        this.brand = "oppo";
    }

    @Data
    public static class TypeDetail {
        private boolean demo;
        private boolean refurbished;
        private boolean retail;
    }

    @Data
    public static class PurchaseDetail {
        private String date;
        private String country;
        private String countryName;
    }

    @Data
    public static class Right {
        private String startDate;
        private String endDate;
        private String code;
        private String name;
        private Integer value;
    }
}