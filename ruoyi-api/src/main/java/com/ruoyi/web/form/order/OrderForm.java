package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("订单参数")
public class OrderForm {

    @ApiModelProperty(value = "省")
    private Long province;


    @ApiModelProperty(value = "订单编码")
    private String orderCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "品牌")
    private String brand;


    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @ApiModelProperty(value = "订单状态", hidden = true)
    private List<Integer> statusList;

    @ApiModelProperty(value = "企业Id", hidden = true)
    private Long companyId;
}
