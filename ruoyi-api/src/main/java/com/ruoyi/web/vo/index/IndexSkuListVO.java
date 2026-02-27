package com.ruoyi.web.vo.index;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("sku列表")
@Accessors(chain = true)
public class IndexSkuListVO {

    @ApiModelProperty("订单编码")
    private String orderCode;

    @ApiModelProperty("平台")
    private String platform;

    @ApiModelProperty("挂单Id")
    private Long hangingOrderId;

    @ApiModelProperty("sku名称")
    private String productName;

    @ApiModelProperty("sku名称")
    private String skuName;

    @ApiModelProperty("数量")
    private Long quantity;

    @ApiModelProperty("省ID")
    private Long province;

    @ApiModelProperty("市Id")
    private Long city;

    @ApiModelProperty("省name")
    private String provinceName;

    @ApiModelProperty("省ID")
    private String cityName;

    @ApiModelProperty("账期类型, 0 T+0 ; 1 T+1; 2 T+2")
    private Integer accountingPeriod;

    @ApiModelProperty("发货时效，0 当天， 1 次日 2 后天")
    private Integer deliveryTime;


    @ApiModelProperty("挂单价（最高）")
    private BigDecimal priceHighest;


    @ApiModelProperty("挂单价（高）")

    private BigDecimal priceHign;


    @ApiModelProperty("挂单价（低）")
    private BigDecimal priceLow;


    @ApiModelProperty("挂单价4（最低价）")
    private BigDecimal priceLowest;


    @ApiModelProperty("最高价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceHighestStatus;


    @ApiModelProperty("高价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceHignStatus;


    @ApiModelProperty("低价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceLowStatus;


    @ApiModelProperty("最低价 挂单价状态（1: 可出价；2:待确认 3.失效）")
    private Integer priceLowestStatus;

    @ApiModelProperty("每次报价时间（单位分钟）")
    private Integer quotationInterval;

    @ApiModelProperty("其他要求")
    private List<String> otherRequire;

    @ApiModelProperty("最后一次抢单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCompeteTime;


    @ApiModelProperty("抢单价格")
    private BigDecimal tradePrice;

    @ApiModelProperty("订单类型： 0 百亿 1 百亿微派'")
    private Integer orderStyle;



}
