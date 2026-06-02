package com.ruoyi.jky.param.goods;

import lombok.Data;

@Data
public class GoodsListParam {

    private Integer pageIndex;

    private Integer pageSize;

    private String goodsNo;

    private String skuBarcode;

    private String goodsName;

    private String skuName;

    private String abcCate;

    private String startDate;

    private String endDate;

    private String startDateModifiedSku;

    private String endDateModifiedSku;

    private String startDateModifiedGoods;

    private String endDateModifiedGoods;

    private Integer isPackageGood;

    private Integer isBlockup;

    private Integer skuIsBlockup;

    private String cateName;

    private String assistBarcode;

    private String goodsNos;

    private String isQueryDelete;

    private String skuBarcodes;

    private String skuCodes;

    private Long maxSkuId;
}
