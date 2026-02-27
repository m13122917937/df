package com.ruoyi.web.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("全部订单vo")
public class AllOrderVO {

    @ApiModelProperty("订单编号")
    private String orderCode;

    @ApiModelProperty("品牌")
    private String brand;
    @ApiModelProperty("分类")
    private String category;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("SKU名称")
    private String skuName;
    @ApiModelProperty("SKU名称")
    private String skuCode;
    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("抢单人")
    private String tradeUserName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("抢单时间")
    private Date tradeTime;

    @ApiModelProperty("发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    @ApiModelProperty("签收时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date signedTime;


    @ApiModelProperty("省")
    private Long province;
    @ApiModelProperty("省")
    private String provinceName;
    @ApiModelProperty("市")
    private Long city;
    @ApiModelProperty("市")
    private String cityName;
    @ApiModelProperty("收件人")
    private String addressee;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("收货地址")
    private String receivingAddress;

    @ApiModelProperty("交易价格")
    private BigDecimal tradePrice;

    @ApiModelProperty("账期")
    private String accountingPeriod;

    @ApiModelProperty("物流单号")
    private String trackingNumber;
    @ApiModelProperty("物流公司")
    private String trackingCompany;

    @ApiModelProperty("撤销类型 撤销原因 0，店铺后台急速退款； 1 顾客拒签/拒收； 2 派送未联系到顾客，退回， 3 24小时物流无揽收信息; 4 供应商私自拦截 5 已经从其他渠道发货")
    private Integer revokeType;

    @ApiModelProperty("订单状态")
    private Integer status;

    @ApiModelProperty("订单子状态")
    private String subStatus;
}
