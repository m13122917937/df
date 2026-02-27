package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单状态下的省数量情况")
public class OrderAreaCountVO {

    @ApiModelProperty("省")
    private String provinceName;

    @ApiModelProperty("省ID")
    private Long province;

    @ApiModelProperty("数量")
    private Integer sum;

}
