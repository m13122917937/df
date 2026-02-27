package com.ruoyi.sn.model;

import lombok.Data;

// Honor（结构与华为几乎一致）
@Data
public class HonorResult extends WarrantyResult {
    private String img;
    private String brandName = "荣耀";
    private String skuItemCode;
    private String product;
    private String productType;
    private String productOffering;
    private TypeDetail type;
    private boolean hasCare;
    private PurchaseDetail purchase;
    private String warrStatus;
    private String covered;
    private Integer daysleft;
    private String productDate;
    private Right[] right;

    {
        this.brand = "荣耀";
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
        private String code;
        private String name;
        private String startDate;
        private String endDate;
    }
}