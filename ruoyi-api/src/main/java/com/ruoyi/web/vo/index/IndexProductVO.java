package com.ruoyi.web.vo.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel("产品VO")
@Accessors(chain = true)
public class IndexProductVO {

    @ApiModelProperty("产品名称")
    private String productName;

    @ApiModelProperty("sku名称")
    private String skuName;

    @ApiModelProperty("数量")
    private Long quantity;
}
