package com.ruoyi.analysis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 返利价保明细，仅映射新表 ana_rebate_detail。
 */
@Data
@TableName("ana_rebate_detail")
public class AnalysisRebateDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private String goodsNo;
    private String goodsName;
    private String snCode;
    private BigDecimal amount;
    private BigDecimal pointRate;
}
