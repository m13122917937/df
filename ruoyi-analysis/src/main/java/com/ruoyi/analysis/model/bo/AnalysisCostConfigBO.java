package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 核算配置业务对象。
 */
@Data
public class AnalysisCostConfigBO {
    private Long id;
    private String configType;
    private LocalDate businessDate;
    private String monthValue;
    private String platform;
    private String shopName;
    private String originalOrderNo;
    private String goodsNo;
    private String goodsName;
    private String brand;
    private String category;
    private BigDecimal amount;
    private BigDecimal coefficient;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String extraData;
    private Long createdBy;
    private LocalDateTime createdTime;
    private Long updatedBy;
    private LocalDateTime updatedTime;
}
