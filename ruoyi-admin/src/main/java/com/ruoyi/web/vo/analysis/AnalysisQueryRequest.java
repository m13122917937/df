package com.ruoyi.web.vo.analysis;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 经营分析 Web 查询请求。
 */
@Data
public class AnalysisQueryRequest {
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
