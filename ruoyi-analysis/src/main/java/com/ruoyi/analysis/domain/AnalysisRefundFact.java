package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 经营分析退款事实，仅映射新表 ana_refund_fact。
 */
@Data
@TableName("ana_refund_fact")
public class AnalysisRefundFact {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String refundKey;
    private String refundNo;
    private String originalOrderNo;
    private String goodsLineKey;
    private String goodsNo;
    private String goodsName;
    private LocalDateTime refundSuccessTime;
    private LocalDate refundDate;
    private BigDecimal refundAmount;
    private BigDecimal platformRefundAmount;
    private BigDecimal refundQuantity;
    private Integer hasGoodsReturn;
    private BigDecimal reversedCost;
    private String matchStatus;
    private String exceptionReason;
    private String rawHash;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
