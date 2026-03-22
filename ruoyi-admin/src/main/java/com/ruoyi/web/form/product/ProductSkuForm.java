package com.ruoyi.web.form.product;

import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
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

public class ProductSkuForm {


    private Long id;


//    @NotBlank(message = "品牌不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String brand;


    @NotBlank(message = "类别不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String category;

//
//    @NotBlank(message = "SPU编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private String spuCode;
//
//
//    @NotBlank(message = "SKU编码不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private String skuCode;


    @NotBlank(message = "商品名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String productName;


    private String specName;

//
//    private String barCode;
//
//
//    private String seriesCode;

//
//    private String seriesName;


//    @NotNull(message = "是否开启序列号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private Integer snType;

//
////    @NotNull(message = "状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
//    private Integer skuStatus;

//
//    private Integer sortOrder;
}