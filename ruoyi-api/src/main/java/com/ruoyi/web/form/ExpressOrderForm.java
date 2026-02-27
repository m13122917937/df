package com.ruoyi.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("快递参数")
public class ExpressOrderForm {

    @ApiModelProperty("订单编号")
    private String orderCode;


    /**
     * 快递单号
     */
    @ApiModelProperty("快递单号")
    private String trackingNumber;

    /**
     * 快递公司
     */
    @ApiModelProperty("快递公司名称")
    private String trackingCompany;

    /**
     * 快递公司
     */
    @ApiModelProperty("快递公司编号")
    private String trackingCompanyCode;

    /**
     * 手机号码
     */
    @ApiModelProperty("手机号码")
    private String cellphone;
}
