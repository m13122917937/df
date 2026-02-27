package com.ruoyi.web.form.rule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("报价")
public class RuleForm {


    @ApiModelProperty("订单编号")
    private String orderCode;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    @ApiModelProperty("0 0天，1 1天 ，2 2天， 3 3天")
    @NotNull(message = "请设置账期类型", groups = {AddGroup.class, UpdateGroup.class})
    private Integer accountingPeriod;

    /**
     * 挂单价（最高）
     */
    @ApiModelProperty("挂单价（最高）")
    @NotNull(message = "请设置挂单价（最高）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceHighest;
    /**
     * 挂单价（高）
     */
    @ApiModelProperty("挂单价（高）")
    @NotNull(message = "请设置挂单价（高）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceHign;
    /**
     * 挂单价（低）
     */
    @ApiModelProperty("挂单价（低）")
    @NotNull(message = "请设置挂单价（低）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceLow;
    /**
     * 挂单价4（最低价）
     */
    @ApiModelProperty("最低价")
    @NotNull(message = "请设置最低价", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceLowest;
    /**
     * 间隔差价
     */
    @ApiModelProperty("间隔差价")
    @NotNull(message = "请设置间隔差价", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal intervalSpread;
    /**
     * 每次报价时间（单位分钟）
     */
    @ApiModelProperty("每次报价时间（单位分钟）")
    @NotNull(message = "请设置每次报价时间", groups = {AddGroup.class, UpdateGroup.class})
    private Long quotationInterval;

    /**
     * 串码选项
     */
    @ApiModelProperty("串码选项, 0  发货前提供串码  1 发货后提供串码 2 不需要串码  ")
    @NotNull(message = "请设置串码选项", groups = {AddGroup.class, UpdateGroup.class})
    private Integer codeOptions;

    /**
     * 其他要求
     */
    @ApiModelProperty("其他要求")
    private String otherRequire;
    /**
     * 规则范围    PROVINCE(0, "省"),
     * COUNTY(1, "国"),
     * SHOP(2, "店铺"),
     * PLATFORM(3, "平台");
     */
    @ApiModelProperty("规则范围,0 省")
    @NotNull(message = "请设置规则范围", groups = {AddGroup.class, UpdateGroup.class})
    private Integer ruleRange;


    @ApiModelProperty("规则范围,0 省")
    @NotNull(message = "请设置规则范围省", groups = {AddGroup.class, UpdateGroup.class})
    private Long province;
    /**
     * 发布信息类型
     */
    @ApiModelProperty("发布信息类型， 0 立即发布； ")
    private Integer releaseType;

    /**
     * 发布时间
     */
    @ApiModelProperty("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;
    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    @ApiModelProperty("发货时效（0：当天；1：明天：2：后天；n：n天后）")
    private Long deliveryTime;


    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    @NotBlank(message = "请设置商品编码", groups = {AddGroup.class, UpdateGroup.class})
    @ApiModelProperty("商品编码")
    private String skuCode;

}
