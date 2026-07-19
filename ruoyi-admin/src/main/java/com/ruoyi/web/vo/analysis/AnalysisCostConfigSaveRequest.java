package com.ruoyi.web.vo.analysis;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 核算配置保存请求。
 */
@Data
public class AnalysisCostConfigSaveRequest {
    private Long id;
    @NotBlank(message = "配置类型不能为空")
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
}
