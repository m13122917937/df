package com.ruoyi.quote.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报价商品实体，映射 quote_product。
 */
@Data
@TableName("quote_product")
public class QuoteProduct {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 品牌ID（引用 quote_brand）
     */
    private Long brandId;

    /**
     * 品类ID（引用 quote_category）
     */
    private Long categoryId;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 品类
     */
    private String category;

    /**
     * 商品名
     */
    private String productName;

    /**
     * 规格/型号
     */
    private String specName;

    /**
     * 零售价
     */
    private BigDecimal retailPrice;

    /**
     * 分销1价
     */
    private BigDecimal distributor1Price;

    /**
     * 分销2价
     */
    private BigDecimal distributor2Price;

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
