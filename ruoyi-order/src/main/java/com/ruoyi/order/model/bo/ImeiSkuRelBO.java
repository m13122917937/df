package com.ruoyi.order.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 型号映射决策对象 o_imei_sku_rel
 *
 * @author ruoyi
 * @date 2025-10-05
 */
@Data
@Accessors(chain = true)
public class ImeiSkuRelBO {
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
     * 确认人ID
     */
    private Long confirmBy;

    /**
     * 确认人姓名
     */
    private String confirmName;

}
