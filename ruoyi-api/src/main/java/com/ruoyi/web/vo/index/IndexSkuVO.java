package com.ruoyi.web.vo.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel("sku数量")
@Accessors(chain = true)
public class IndexSkuVO {


    @ApiModelProperty("sku名称")
    private String skuName;

    @ApiModelProperty("数量")
    private Long quantity;


}
