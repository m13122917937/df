package com.ruoyi.web.vo.analysis;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
/** 回款周期响应。 */
@Data
public class AnalysisCollectionCycleConfigVO {
    private Long id; private String platform; private String shopName;
    private Integer goodsCollectionDays; private Integer subsidyCollectionDays; private Integer nationalSubsidyCollectionDays;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedTime;
}
