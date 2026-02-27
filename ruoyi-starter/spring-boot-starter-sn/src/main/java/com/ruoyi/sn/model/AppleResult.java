package com.ruoyi.sn.model;

import lombok.Data;

// Apple
@Data
public class AppleResult extends WarrantyResult {
    private String serial;
    private String thumbnail;
    private boolean replaced;
    private boolean registered;
    private String loaner;
    private boolean validPurchaseDate;
    private Integer warrantyDaysRemaining;
    private boolean acEligible;
    private String estPurchaseDate;
    private String activeDate;
    private String repairExpiry;
    private Integer warrantyYear;
    private boolean appleCare;
    private String acExpiry;
    private String acDaysRemaining;
    private String storage;
    private String color;
    private String number; // 入网型号

    {
        this.brand = "Apple";
    }
}