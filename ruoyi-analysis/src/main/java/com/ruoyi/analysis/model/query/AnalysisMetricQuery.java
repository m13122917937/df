package com.ruoyi.analysis.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;

import java.time.LocalDate;

/**
 * 每日经营指标动态查询条件。
 */
@Data
public class AnalysisMetricQuery {
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "metric_date")
    private LocalDate startDate;
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "metric_date")
    private LocalDate endDate;
    private String subjectName;
    private String orderType;
    private String platform;
    private String shopName;
    private String brand;
    private String category;
    private String goodsNo;
    private String calcStatus;
}
