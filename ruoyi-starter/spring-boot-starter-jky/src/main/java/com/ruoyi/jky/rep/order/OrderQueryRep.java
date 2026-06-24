package com.ruoyi.jky.rep.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderQueryRep {

    private String scrollId;

    private List<OrderTradeRep> trades;

    @Data
    public static class OrderTradeRep {

        private String tradeNo;

        private String orderNo;

        private String tradeId;

        private Integer tradeStatus;

        private String tradeTime;

        private String customerTradeNo;

        private String payTime;

        private String consignTime;

        private String auditTime;

        private String completeTime;

        private String signingTime;

        private Long shopId;

        private String shopName;

        private String shopTypeCode;

        private String companyName;

        private Long warehouseId;

        private String warehouseName;

        private String lastShipTime;

        private String logisticName;

        private String mainPostid;

        private String sourceTradeNo;

        private String onlineTradeNo;

        private String pickUpCode;

        private String billDate;

        private String customizeGoodsColumn9;

        private String compassSourceContentTypem;

        private List<OrderExpenseRep> expense;

        private BigDecimal totalFee;

        private BigDecimal payment;

        private BigDecimal discountFee;

        private String sellerMemo;

        private String buyerMemo;

        private String payerName;

        private String payerPhone;

        private String payerAddress;

        private String flagNames;

        private String platFlags;

        private List<OrderGoodsDetailRep> goodsDetail;

    }

    @Data
    public static class OrderExpenseRep {

        private BigDecimal expenseFee;

        private String expenseItemName;

    }

    @Data
    public static class OrderGoodsDetailRep {

        private String goodsId;

        private String goodsNo;

        private String goodsName;

        private String specName;

        private BigDecimal sellCount;

        private BigDecimal needProcessCount;

        private BigDecimal baseUnitSellCount;

        private BigDecimal sellPrice;

        private BigDecimal sellTotal;

        private Integer isGift;

        private String outerId;

        private BigDecimal assessmentCost;

        private String compassSourceContentTypem;

        private BigDecimal goodsPlatDiscountFee;

        private BigDecimal shareOrderDiscountFee;

        private BigDecimal shareOrderPlatDiscountFee;

    }

}
