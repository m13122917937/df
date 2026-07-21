package com.ruoyi.web.vo.analysis;
import lombok.Data;
import java.time.LocalDateTime;
/** 回款周期响应。 */
@Data
public class AnalysisCollectionCycleConfigVO {
    private Long id; private String platform; private String shopName;
    private Integer goodsCollectionDays; private Integer subsidyCollectionDays; private Integer nationalSubsidyCollectionDays;
    private LocalDateTime updatedTime;
}
