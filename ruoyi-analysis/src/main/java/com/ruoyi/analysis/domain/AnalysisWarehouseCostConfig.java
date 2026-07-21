package com.ruoyi.analysis.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/** 仓储售后成本配置。 */
@Data @TableName("ana_warehouse_cost_config")
public class AnalysisWarehouseCostConfig {
    @TableId(type = IdType.AUTO) private Long id;
    private String monthValue; private BigDecimal afterSalesHeadcount; private BigDecimal afterSalesLaborCost;
    private Long createdBy; private LocalDateTime createdTime; private Long updatedBy; private LocalDateTime updatedTime;
}
