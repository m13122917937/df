package com.ruoyi.analysis.model.bo;

import lombok.Data;
import java.time.LocalDateTime;

/** 回款周期业务对象。 */
@Data
public class AnalysisCollectionCycleConfigBO {
    private Long id;
    private String platform;
    private String shopName;
    private Integer goodsCollectionDays;
    private Integer subsidyCollectionDays;
    private Integer nationalSubsidyCollectionDays;
    private LocalDateTime updatedTime;
}
