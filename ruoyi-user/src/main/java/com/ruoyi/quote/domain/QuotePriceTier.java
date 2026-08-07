package com.ruoyi.quote.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报价价格档位实体，映射 quote_price_tier。
 */
@Data
@TableName("quote_price_tier")
public class QuotePriceTier {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 档位名称
     */
    private String tierName;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 逻辑删除标记(0-正常，1-删除)
     */
    @TableLogic
    private Long deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
