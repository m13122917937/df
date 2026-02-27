package com.ruoyi.web.vo.product;

import io.swagger.annotations.ApiModelProperty;
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
public class ProductVO {
    private static final long serialVersionUID = 1L;


    /** 品牌 */
    @ApiModelProperty("品牌")
    private String brand;
    /** 类别 */
    @ApiModelProperty("类别")
    private String category;
    /** spu编码 */
    @ApiModelProperty("spu编码")
    private String spuCode;
    /** sku编码 */
    @ApiModelProperty("sku编码")
    private String skuCode;
    /** 商品名 */
    @ApiModelProperty("商品名")
    private String productName;
    /** 规格名 */
    @ApiModelProperty("规格名")
    private String specName;
    /** 商品条码 */
    @ApiModelProperty("商品条码")
    private Integer snType;


}
