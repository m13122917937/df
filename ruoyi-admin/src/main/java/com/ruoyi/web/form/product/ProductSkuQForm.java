package com.ruoyi.web.form.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductSkuQForm {

    @ApiModelProperty(value = "品牌")
    String brand;
    @ApiModelProperty(value = "品类")
    String category;
    @ApiModelProperty(value = "商品名")
    String productName;

}
