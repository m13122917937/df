package com.ruoyi.analysis.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.Data;

/**
 * 平台服务费率配置查询参数。
 */
@Data
public class AnalysisPlatformFeeRateQuery {
    private String platform;

    private Integer businessType;

    private String category;
}
