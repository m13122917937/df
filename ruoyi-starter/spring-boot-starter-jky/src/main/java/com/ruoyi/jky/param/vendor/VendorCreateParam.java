package com.ruoyi.jky.param.vendor;

import lombok.Data;

import java.util.List;

@Data
public class VendorCreateParam {

    private String code;

    private String name;

    private String abbreviation;

    private String classCode;

    private String className;

    private String levelCode;

    private String assistantCode;

    private String leader;

    private String developDate;

    private String businessScope;

    private String memo;

    private String flagData;

    private String countryName;

    private String provinceName;

    private String cityName;

    private String townName;

    private String streetName;

    private String address;

    private String postcode;

    private String email;

    private String website;

    private String tel;

    private String fax;

    private List<VendorLinkman> vendLinkmanList;

    private List<VendorPayAccount> vendPayAccountList;

    private List<VendorPurchUser> vendPurchUserList;

    private List<VendorSeller> vendSellerList;

    @Data
    public static class VendorLinkman {

        private String linkman;

        private String position;

        private String linktel;

        private String qq;

        private String wechat;

        private String email;

        private String memo;

    }

    @Data
    public static class VendorPayAccount {

        private String accountTypeName;

        private String bankbranch;

        private String bankacct;

        private String accName;

        private String bankName;

        private Integer isDefault;

    }

    @Data
    public static class VendorPurchUser {

        private Long id;

        private Long companyId;

        private String companyName;

        private Long purchUserId;

        private String purchUserName;

        private String purchOfficeName;

        private Integer isDefault;

    }

    @Data
    public static class VendorSeller {

        private String sellerName;

        private String sellerTaxCode;

        private String sellerBankAccount;

        private String sellerAddressPhone;

        private Integer isDefault;

    }

}
