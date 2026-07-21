package com.ruoyi.web.vo.analysis;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data public class AnalysisWarehouseCostConfigVO { private Long id; private String monthValue; private BigDecimal afterSalesHeadcount; private BigDecimal afterSalesLaborCost; private LocalDateTime updatedTime; }
