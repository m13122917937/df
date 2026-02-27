package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class OrderExpressVO {

    /**
     * 快递公司编码
     */
    @ApiModelProperty("快递公司编码")
    private String logisticsCode;
    /**
     * 快递单号
     */
    @ApiModelProperty("快递单号")
    private String logisticsNo;


    @ApiModelProperty("手机号")
    private String phone;
}
