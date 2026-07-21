package com.ruoyi.analysis.model.bo;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 仓储售后成本业务对象。 */
@Data public class AnalysisWarehouseCostConfigBO {
    private Long id; private String monthValue; private BigDecimal afterSalesHeadcount; private BigDecimal afterSalesLaborCost; private LocalDateTime updatedTime;
}
