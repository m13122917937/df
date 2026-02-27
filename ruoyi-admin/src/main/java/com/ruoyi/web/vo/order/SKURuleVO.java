package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品编码")
public class SKURuleVO {

    @ApiModelProperty("商品编码")
    private String skuCode;
    @ApiModelProperty("sku商品名称")
    private String skuName;
    @ApiModelProperty("商品名称")
    private String productName;
}
