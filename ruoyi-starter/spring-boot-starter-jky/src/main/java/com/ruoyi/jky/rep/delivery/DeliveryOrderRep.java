package com.ruoyi.jky.rep.delivery;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DeliveryOrderRep {

    private Integer totalPage;

    private List<DeliveryOrder> deliveryOrder;

    private PageInfo pageInfo;

    @Data
    public static class DeliveryOrder {

        private String ownerCode;

        private String warehouseCode;

        private String deliveryOrderCode;

        private String preDeliveryOrderCode;

        private String platOrderNO;

        private String sourcePlatformCode;

        private String shopNick;

        private String logisticsCode;

        private String logisticName;

        private String expressCode;

        private String logisticNO;

        private BigDecimal totalAmount;

        private String createTime;

        private String placeOrderTime;

        private String payTime;

        private List<OrderLine> orderLines;

        private String logisticsNo;

        private Integer goodsKinds;

        private BigDecimal goodsTotal;

        private String jitPoNos;

        private String jitStorageNo;

        private String jitWarehouse;

        private String registerTime;

        private String flagNames;

    }

    @Data
    public static class OrderLine {

        private String orderLineNo;

        private String itemCode;

        private Long itemId;

        private String inventoryType;

        private String goodsCode;

        private String itemName;

        private String skuProperty;

        private String barcode;

        private String unit;

        private BigDecimal planQty;

        private Integer isGift;

        private String batchCode;

        private String productDate;

        private String expireDate;

        private Long positionsId;

        private String positionsName;

    }

    @Data
    public static class PageInfo {

        private Integer pageIndex;

        private Integer offset;

        private Integer pageSize;

        private Integer total;

    }

}
