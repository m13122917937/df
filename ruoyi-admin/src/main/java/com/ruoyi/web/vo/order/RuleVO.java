package com.ruoyi.web.vo.order;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class RuleVO {


    /**
     * 主键
     */

    private Long id;
    /**
     * 省
     */

    private Long province;



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

    private String productName;
    /**
     * 通用sku
     */

    private String skuName;

    /**
     * 通用sku
     */

    private String skuCode;


    /**
     * 挂单价（最高）
     */

    private BigDecimal priceHighest;
    /**
     * 挂单价（高）
     */

    private BigDecimal priceHign;
    /**
     * 挂单价（低）
     */

    private BigDecimal priceLow;
    /**
     * 挂单价4（最低价）
     */

    private BigDecimal priceLowest;

    /**
     * 账期类型（1:字典；2:自定义）
     */

    private Long accountingPeriod;

    /**
     * 间隔差价
     */

    private BigDecimal intervalSpread;

    /**
     * 每次报价时间（单位分钟）
     */

    private Integer quotationInterval;

    /**
     * 串码选项
     */

    private Integer codeOptions;

    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */

    private Long deliveryTime;

    /**
     * 其他要求
     */

    private String otherRequire;


    /**
     * 状态（1: 有效；2:无效）
     */

    private Long status;

}
