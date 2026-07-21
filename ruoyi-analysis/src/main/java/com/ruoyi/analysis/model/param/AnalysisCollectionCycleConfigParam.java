package com.ruoyi.analysis.model.param;

import lombok.Data;

/** 回款周期保存参数。 */
@Data
public class AnalysisCollectionCycleConfigParam {
    private Long id;
    private String platform;
    private String shopName;
    private Integer goodsCollectionDays;
    private Integer subsidyCollectionDays;
    private Integer nationalSubsidyCollectionDays;
    private Long operatorId;
}
