package com.ruoyi.analysis.model.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 经营分析统一查询条件。
 */
@Data
public class AnalysisQuery {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String period;
    private String subjectName;
    private String orderType;
    private String platform;
    private String shopName;
    private String brand;
    private String category;
    private String goodsNo;
    private String calcStatus;
    private String configType;
}
