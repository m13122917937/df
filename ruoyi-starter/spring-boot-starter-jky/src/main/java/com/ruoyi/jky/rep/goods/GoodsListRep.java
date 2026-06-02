package com.ruoyi.jky.rep.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsListRep {

    private Long goodsId;

    private String goodsNo;

    private String goodsName;

    private Long skuId;

    private String skuName;

    private String skuBarcode;

    private String skuNo;

    private String unitName;

    private String abcCate;

    private Integer brandId;

    private String brandName;

    private Integer cateId;

    private String cateName;

    private String cateFullName;

    private Integer goodsAttr;

    private String isDelete;

    private Integer isPackageGood;

    private BigDecimal skuLength;

    private BigDecimal skuWidth;

    private BigDecimal skuHeight;

    private BigDecimal skuWeight;

    private BigDecimal volume;

    private String skuImgUrl;

    private List<ImgUrlRep> imgUrlList;

    private String gmtCreate;

    private String skuGmtCreate;

    private String goodsGmtModified;

    private String skuGmtModified;

    private Long defaultVendId;

    private String defaultVendName;

    private Long warehouseId;

    private String warehouseName;

    private Integer isBatchMgmt;

    private Integer isSerialManagement;

    private Boolean isStopSelling;

    private Boolean isStopPurchasing;

    private Boolean inStoreProcessing;

    private String goodsField1;
    private String goodsField2;
    private String goodsField3;
    private String goodsField4;
    private String goodsField5;
    private String goodsField6;
    private String goodsField7;
    private String goodsField8;
    private String goodsField9;
    private String goodsField10;
    private String goodsField11;
    private String goodsField12;
    private String goodsField13;
    private String goodsField14;
    private String goodsField15;
    private String goodsField16;
    private String goodsField17;
    private String goodsField18;
    private String goodsField19;
    private String goodsField20;
    private String goodsField21;
    private String goodsField22;
    private String goodsField23;
    private String goodsField24;
    private String goodsField25;
    private String goodsField26;
    private String goodsField27;
    private String goodsField28;
    private String goodsField29;
    private String goodsField30;
    private String goodsField31;
    private String goodsField32;
    private String goodsField33;
    private String goodsField34;
    private String goodsField35;
    private String goodsField36;
    private String goodsField37;
    private String goodsField38;
    private String goodsField39;
    private String goodsField40;
    private String goodsField41;
    private String goodsField42;
    private String goodsField43;
    private String goodsField44;
    private String goodsField45;
    private String goodsField46;
    private String goodsField47;
    private String goodsField48;
    private String goodsField49;
    private String goodsField50;

    private String skuField1;
    private String skuField2;
    private String skuField3;
    private String skuField4;
    private String skuField5;
    private String skuField6;
    private String skuField7;
    private String skuField8;
    private String skuField9;
    private String skuField10;
    private String skuField11;
    private String skuField12;
    private String skuField13;
    private String skuField14;
    private String skuField15;
    private String skuField16;
    private String skuField17;
    private String skuField18;
    private String skuField19;
    private String skuField20;
    private String skuField21;
    private String skuField22;
    private String skuField23;
    private String skuField24;
    private String skuField25;
    private String skuField26;
    private String skuField27;
    private String skuField28;
    private String skuField29;
    private String skuField30;

    @Data
    public static class ImgUrlRep {

        private Long goodsId;

        private String imgUrl;

        private String isMainImage;

        private String imagePosition;

        private String imgKey;
    }
}
