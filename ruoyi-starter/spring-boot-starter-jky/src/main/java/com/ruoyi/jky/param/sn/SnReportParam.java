package com.ruoyi.jky.param.sn;

import lombok.Data;

import java.util.List;

@Data
public class SnReportParam {

    private String deliveryorderno;

    private String deliverytype;

    private String ownerorderno;

    private String warehousecode;

    private String ownercode;

    private String outbizcode;

    private String operatorcode;

    private String operatorname;

    private String operatetime;

    private List<SnReportGoods> goods;

    @Data
    public static class SnReportGoods {

        private String outSkuCode;

        private String name;

        private String itemid;

        private String unit;

        private String barcode;

        private List<String> snlist;

    }

}
