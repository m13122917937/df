package com.ruoyi.web.vo.analysis;
import lombok.Data;
import java.math.BigDecimal;
@Data public class AnalysisWarehouseCostConfigSaveRequest { private Long id; private String monthValue; private BigDecimal afterSalesHeadcount; private BigDecimal afterSalesLaborCost; }
