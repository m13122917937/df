package com.ruoyi.web.form.rule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data

public class RuleForm {



    private String orderCode;

    /**
     * 账期类型（1:字典；2:自定义）
     */

    @NotNull(message = "请设置账期类型", groups = {AddGroup.class, UpdateGroup.class})
    private Integer accountingPeriod;

    /**
     * 挂单价（最高）
     */

    @NotNull(message = "请设置挂单价（最高）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceHighest;
    /**
     * 挂单价（高）
     */

    @NotNull(message = "请设置挂单价（高）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceHign;
    /**
     * 挂单价（低）
     */

    @NotNull(message = "请设置挂单价（低）", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceLow;
    /**
     * 挂单价4（最低价）
     */

    @NotNull(message = "请设置最低价", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal priceLowest;
    /**
     * 间隔差价
     */

    @NotNull(message = "请设置间隔差价", groups = {AddGroup.class, UpdateGroup.class})
    private BigDecimal intervalSpread;
    /**
     * 每次报价时间（单位分钟）
     */

    @NotNull(message = "请设置每次报价时间", groups = {AddGroup.class, UpdateGroup.class})
    private Long quotationInterval;

    /**
     * 串码选项
     */

    @NotNull(message = "请设置串码选项", groups = {AddGroup.class, UpdateGroup.class})
    private Integer codeOptions;

    /**
     * 其他要求
     */

    private String otherRequire;
    /**
     * 规则范围    PROVINCE(0, "省"),
     * COUNTY(1, "国"),
     * SHOP(2, "店铺"),
     * PLATFORM(3, "平台");
     */

    @NotNull(message = "请设置规则范围", groups = {AddGroup.class, UpdateGroup.class})
    private Integer ruleRange;



    @NotNull(message = "请设置规则范围省", groups = {AddGroup.class, UpdateGroup.class})
    private Long province;
    /**
     * 发布信息类型
     */

    private Integer releaseType;

    /**
     * 发布时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date releaseTime;
    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */

    private Long deliveryTime;


    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    @NotBlank(message = "请设置商品编码", groups = {AddGroup.class, UpdateGroup.class})

    private String skuCode;

}
