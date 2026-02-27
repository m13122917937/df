package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单状态下的品牌数量情况")
public class OrderBrandCountVO {

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("数量")
    private Integer sum;

}
