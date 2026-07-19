package com.ruoyi.analysis.model.source;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 吉客云退款商品行转换为退款事实前的计算结果。
 */
@Value
@Builder
public class AnalysisRefundFactSource {
    String refundKey;
    String refundNo;
    String originalOrderNo;
    String goodsLineKey;
    String goodsNo;
    String goodsName;
    LocalDateTime refundSuccessTime;
    LocalDate refundDate;
    BigDecimal refundAmount;
    BigDecimal platformRefundAmount;
    BigDecimal refundQuantity;
    Integer hasGoodsReturn;
    BigDecimal reversedCost;
    String matchStatus;
    String rawHash;
}
