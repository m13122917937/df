package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("查询规则列表")
public class RuleVO {


    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * 省
     */
    @ApiModelProperty("省id")
    private Long province;


    @ApiModelProperty("省名称")
    private String provinceName;

    /**
     * 品牌
     */
    private String brand;
    /**
     * 类别
     */
    private String category;

    /**
     * 产品型号
     */
    @ApiModelProperty("产品型号")
    private String productName;
    /**
     * 通用sku
     */
    @ApiModelProperty("通用sku")
    private String skuName;

    /**
     * 通用sku
     */
    @ApiModelProperty("通用sku")
    private String skuCode;


    /**
     * 挂单价（最高）
     */
    @ApiModelProperty("挂单价（最高）")
    private BigDecimal priceHighest;
    /**
     * 挂单价（高）
     */
    @ApiModelProperty("挂单价（高）")
    private BigDecimal priceHign;
    /**
     * 挂单价（低）
     */
    @ApiModelProperty("挂单价（低）")
    private BigDecimal priceLow;
    /**
     * 挂单价4（最低价）
     */
    @ApiModelProperty("挂单价4（最低价）")
    private BigDecimal priceLowest;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    @ApiModelProperty("账期类型, 0 ")
    private Long accountingPeriod;

    /**
     * 间隔差价
     */
    @ApiModelProperty("间隔差价")
    private BigDecimal intervalSpread;

    /**
     * 每次报价时间（单位分钟）
     */
    @ApiModelProperty("每次报价时间")
    private Integer quotationInterval;

    /**
     * 串码选项
     */
    @ApiModelProperty("串码选项")
    private Integer codeOptions;

    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    @ApiModelProperty("发货时效0：当天；1：明天：2：后天；n：n天后")
    private Long deliveryTime;

    /**
     * 其他要求
     */
    @ApiModelProperty("其他要求")
    private String otherRequire;


    /**
     * 状态（1: 有效；2:无效）
     */
    @ApiModelProperty("状态（1: 有效；2:无效）")
    private Long status;

}
