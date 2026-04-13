package com.ruoyi.order.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 型号映射决策对象 o_imei_sku_rel
 *
 * @author ruoyi
 * @date 2025-10-05
 */
@Data
@Accessors(chain = true)
public class ImeiSkuRelQuery {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 类别
     */
    private String category;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 状态 1待处理 2已对应 3已取消
     */
    private Integer status;
    /**
     * SKU编码
     */
    private String skuCode;
    /**
     * 型号
     */
    private String productName;

    /**
     * 型号
     */
    @QueryField(operator = DynamicCondition.Operator.IN, field = "product_name")
    private String productNameLike;
    /**
     * 型号
     */
    private String skuName;
    /**
     * 识别的skuId
     */
    private String snSkuId;
    /**
     * 识别型号
     */
    private String snModel;
    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Long deleted;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;


    /**
     * 修改时间
     */
    @QueryField(operator = DynamicCondition.Operator.LIMIT)
    private Integer limit;


}
