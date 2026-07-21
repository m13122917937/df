package com.ruoyi.analysis.model.param;
import lombok.Data;
import java.math.BigDecimal;
/** 仓储售后成本保存参数。 */
@Data public class AnalysisWarehouseCostConfigParam {
    private Long id; private String monthValue; private BigDecimal afterSalesHeadcount; private BigDecimal afterSalesLaborCost; private Long operatorId;
}
