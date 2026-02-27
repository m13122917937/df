package com.ruoyi.rule.model.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 规则对象 o_rule
 *
 * @author ruoyi
 * @date 2025-09-10
 */
@Data
@Accessors(chain = true)
public class RuleBO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 省
     */
    private Long province;

    /**
     * 平台
     */
    private String platform;


    /**
     * 店铺
     */
    private String shopName;

    /**
     * 账期类型（1:字典；2:自定义）
     */
    private Integer accountingPeriod;

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
     * 间隔差价
     */
    private BigDecimal intervalSpread;
    /**
     * 每次报价时间（单位分钟）
     */
    private Long quotationInterval;

    /**
     * 串码选项
     */
    private Long codeOptions;


    /**
     * 其他要求
     */
    private String otherRequire;
    /**
     * 规则范围
     */
    private Integer ruleRange;
    /**
     * 发布信息类型
     */
    private Integer releaseType;

    /**
     * 发布时间
     */
    private Date releaseTime;
    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    private Integer deliveryTime;
    /**
     * 商品编码
     */
    private String skuCode;
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
     * 状态（1: 有效；2:无效）
     */
    private Long status;
    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Long deleted;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;


}
