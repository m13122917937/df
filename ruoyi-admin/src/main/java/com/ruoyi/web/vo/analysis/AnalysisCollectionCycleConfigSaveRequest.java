package com.ruoyi.web.vo.analysis;
import lombok.Data;
/** 回款周期保存请求。 */
@Data
public class AnalysisCollectionCycleConfigSaveRequest {
    private Long id; private String platform; private String shopName;
    private Integer goodsCollectionDays; private Integer subsidyCollectionDays; private Integer nationalSubsidyCollectionDays;
}
