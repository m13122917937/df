package com.ruoyi.jky.rep.refund;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RefundQueryRep {

    private Integer count;

    private List<RefundTradeRep> tradeAfterOnlineDtoArr;

    @Data
    public static class RefundTradeRep {

        private Long tradeAfterOnlineId;

        private String refundNo;

        private String platOrderNo;

        private String customerName;

        private String customerAccount;

        private BigDecimal refundAmount;

        private BigDecimal platRefundAmount;

        private String refundStatus;

        private String refundStatusExplain;

        private String refundSuccessTime;

        private String refundTimeCreate;

        private String refundTimeModified;

        private Integer refundPhase;

        private String refundPhaseExplain;

        private String refundDesc;

        private String reason;

        private Integer hasGoodsReturn;

        private String orderStatus;

        private String orderStatusExplain;

        private String curStatus;

        private String curStatusExplain;

        private String goodsStatus;

        private String goodsStatusExplain;

        private String platName;

        private String shopId;

        private String shopName;

        private String warehouseName;

        private String logisticName;

        private String mainPostid;

        private String province;

        private String city;

        private String district;

        private String town;

        private String address;

        private String phone;

        private String mobile;

        private String name;

        private String buyerMemo;

        private String sellerMemo;

        private String sysFlagIds;

        private String gmtCreate;

        private String gmtModified;

        private String platOrderCreateTime;

        private List<RefundGoodsRep> tradeAfterOnlineGoodsDTOList;

    }

    @Data
    public static class RefundGoodsRep {

        private String goodsId;

        private String goodsNo;

        private String outerId;

        private String outerSkuId;

        private String goodsName;

        private String specName;

        private String tradeGoodsName;

        private String tradeGoodsSpec;

        private String platSkuId;

        private String platGoodsId;

        private String subPlatOrderNo;

        private String unit;

        private BigDecimal sellCount;

        private BigDecimal price;

        private BigDecimal sellTotal;

        private BigDecimal discountFee;

        private Integer isFit;

        private Integer isGift;

        private Integer isVirtual;

        private String barcode;

        private String reasonDesc;

        private String type;

    }

}
