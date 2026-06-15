package com.ruoyi.jky.param.inspect;

import lombok.Data;

import java.util.List;

@Data
public class InspectParam {

    private String number;

    private String boxNoList;

    private Long checkerId;

    private String checker;

    private List<InspectDetail> inspectDetailList;

    private String materialList;

    private Integer isRefundDetect;

    private Integer isCheckOrder;

    private Integer isCheckSnWare;

    private Integer isOwnerMaterial;

    private Integer isSnQuerySku;

    @Data
    public static class InspectDetail {

        private Long skuId;

        private String skuBarcode;

        private String goodsNo;

        private String skuName;

        private String snList;

    }

}
