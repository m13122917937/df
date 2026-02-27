package com.ruoyi.product.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


import java.util.Date;

/**
 * 商品基础数据对象 p_product_sku
 *
 * @author ruoyi
 * @date 2025-09-21
 */
@Data
@Accessors(chain = true)
@TableName("p_product_sku")
public class ProductSku {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long id;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 类别
     */
    private String category;
    /**
     * spu编码
     */
    private String spuCode;
    /**
     * sku编码
     */
    private String skuCode;
    /**
     * 商品名
     */
    private String productName;
    /**
     * 规格名
     */
    private String specName;
    /**
     * 商品条码
     */
    private String barCode;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 是否开启序列号(0-不需要，1-需要)
     */
    private Integer snType;

    /**
     * 是否删除
     */
    @TableLogic
    private Long deleted;
    /**
     * 排序
     */
    private Long sortOrder;


}
