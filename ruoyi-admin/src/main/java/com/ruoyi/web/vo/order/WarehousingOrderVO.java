package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("入仓订单")
public class WarehousingOrderVO {

    @ApiModelProperty("订单编号")
    private String orderCode;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String tradeUserName;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("分类")
    private String category;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("规格名称")
    private String skuName;

    @ApiModelProperty("成交数量")
    private Integer quantity;

    @ApiModelProperty("已经入仓数量")
    private Integer warehouseQuantity;

    @ApiModelProperty("成交价格")
    private BigDecimal tradePrice;

    @ApiModelProperty("成交企业名称")
    private String tradeCompanyName;

    @ApiModelProperty("送货码")
    private Integer deliveryCode;


    @ApiModelProperty("备注")
    private String remark;

}
