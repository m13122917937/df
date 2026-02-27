package com.ruoyi.web.form.product;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 商品SKU表单对象
 *
 * @author ruoyi
 * @date 2025-12-17
 */
@Data
@ApiModel("商品SKU表单")
public class ProductSkuForm {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty(value = "品牌", required = true)
//    @NotBlank(message = "品牌不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String brand;

    @ApiModelProperty(value = "类别", required = true)
    @NotBlank(message = "类别不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String category;

//    @ApiModelProperty(value = "SPU编码", required = true)
//    @NotBlank(message = "SPU编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private String spuCode;
//
//    @ApiModelProperty(value = "SKU编码", required = true)
//    @NotBlank(message = "SKU编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private String skuCode;

    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String productName;

    @ApiModelProperty("规格名")
    private String specName;

//    @ApiModelProperty("商品条码")
//    private String barCode;
//
//    @ApiModelProperty("系列编码")
//    private String seriesCode;

//    @ApiModelProperty("系列名称")
//    private String seriesName;

    @ApiModelProperty(value = "是否开启序列号(0:不启用, 1:序列号, 2:随机序列号)", required = true)
//    @NotNull(message = "是否开启序列号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer snType;

//    @ApiModelProperty(value = "状态（0：禁用、1：正常）", required = true)
////    @NotNull(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private Integer skuStatus;

//    @ApiModelProperty("排序")
//    private Integer sortOrder;
}