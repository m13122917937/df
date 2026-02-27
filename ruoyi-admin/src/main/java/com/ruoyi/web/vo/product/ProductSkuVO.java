package com.ruoyi.web.vo.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 商品SKU视图对象
 *
 * @author ruoyi
 * @date 2025-12-17
 */
@Data
@ApiModel("商品SKU视图对象")
public class ProductSkuVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("类别")
    private String category;

    @ApiModelProperty("SPU编码")
    private String spuCode;

    @ApiModelProperty("SKU编码")
    private String skuCode;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("规格名")
    private String specName;


    @ApiModelProperty("创建时间")
    private Date createTime;


    @ApiModelProperty("是否开启序列号(0-不需要，1-需要)")
    private Long snType;


}