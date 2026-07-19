package com.ruoyi.analysis.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;

import java.time.LocalDate;

/**
 * 核算配置动态查询条件。
 */
@Data
public class AnalysisCostConfigQuery {
    @QueryField(operator = DynamicCondition.Operator.GTE, field = "business_date")
    private LocalDate startDate;
    @QueryField(operator = DynamicCondition.Operator.LTE, field = "business_date")
    private LocalDate endDate;
    private String configType;
    private String platform;
    private String shopName;
    private String brand;
    private String category;
    private String goodsNo;
}
