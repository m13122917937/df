package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 物流订阅参数
 */
@Data
@ApiModel
public class LogisticsSubscribeParam {
    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderCode;

    /**
     * 物流公司编码
     */
    @ApiModelProperty("物流公司编码")
    private String logisticsCode;

    /**
     * 物流单号
     */
    @ApiModelProperty("物流单号")
    private String logisticsNo;

    /**
     * 收件人手机号
     */
    @ApiModelProperty("手机号")
    private String phone;


}