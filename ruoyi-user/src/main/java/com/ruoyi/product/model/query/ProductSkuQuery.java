package com.ruoyi.product.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 商品基础数据对象 p_product_sku
 *
 * @author ruoyi
 * @date 2025-09-21
 */
@Data
@Accessors(chain = true)
public class ProductSkuQuery {
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
     * 商品名
     */
    @QueryField(operator = DynamicCondition.Operator.LIKE, field = "product_name")
    private String productNameLike;
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
     * 系列编码
     */
    private String seriesCode;
    /**
     * 系列名称
     */
    private String seriesName;
    /**
     * 是否开启序列号(0-不需要，1-需要)
     */
    private Long isSeriesNumber;
    /**
     * 状态（0：禁用、1：正常）
     */
    private Long skuStatus;
    /**
     * 是否删除
     */
    private Long deleted;
    /**
     * 排序
     */
    private Long sortOrder;

    /**
     * 排序
     */
    @QueryField(operator = DynamicCondition.Operator.LIMIT)
    private Integer limit;

    @QueryField(operator = DynamicCondition.Operator.GROUP, field = "sku_code")
    private String group;



}
