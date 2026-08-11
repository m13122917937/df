package com.ruoyi.subsidy.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 国补商城库存流水。
 */
@Data
@Accessors(chain = true)
@TableName("gb_stock_log")
public class GbStockLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private Long orderId;
    private Integer changeQuantity;
    private Integer afterQuantity;
    private String changeType;
    private String remark;
    private Date createTime;
}
