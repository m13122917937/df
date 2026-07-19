package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 核算配置响应对象。
 */
@Data
public class AnalysisCostConfigVO {
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
