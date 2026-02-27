package com.ruoyi.order.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 挂单对象 o_hanging_order
 *
 * @author ruoyi
 * @date 2025-09-09
 */
@Data
@Accessors(chain = true)
public class HangingOrderParam {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 订单表id
     */
    private String orderId;
    /**
     * 挂单价（最高）
     */
    private BigDecimal priceHighest;
    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    private Integer priceHighestStatus;
    /**
     * 挂单价（高）
     */
    private BigDecimal priceHign;
    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    private Integer priceHignStatus;
    /**
     * 挂单价（低）
     */
    private BigDecimal priceLow;
    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    private Integer priceLowStatus;
    /**
     * 挂单价4（最低价）
     */
    private BigDecimal priceLowest;
    /**
     * 挂单价状态（1: 可出价；2:待确认 3.失效）
     */
    private Integer priceLowestStatus;

    /**
     * 每次报价时间（单位分钟）
     */
    private Long quotationInterval;
    /**
     * 账期类型（1:字典；2:自定义）
     */
    private Integer accountingPeriod;

    /**
     * 快递要求（默认）
     */
    private String logisticRequire;
    /**
     * 发布信息类型
     */
    private Integer releaseType;

    /**
     * 发布时间
     */
    private Date releaseTime;
    /**
     * 挂单状态，描述是否撤销挂单（1: 有效；2:无效）
     */
    private Integer status;
    /**
     * 最后一次抢单用户
     */
    private Long lastCompeteUser;
    /**
     * 最后一次抢单企业
     */
    private Long lastCompeteCompany;
    /**
     * 最后一次抢单时间
     */
    private Date lastCompeteTime;
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
    /**
     * 税票要求
     */
    private Long taxRequire;
    /**
     * 串码选项
     */
    private Integer codeOptions;
    /**
     * 包装要求
     */
    private String packingRequire;
    /**
     * 其他要求
     */
    private String otherRequire;
    /**
     * 间隔差价
     */
    private BigDecimal intervalSpread;
    /**
     * 规则范围
     */
    private Long ruleRange;
    /**
     * 规则id
     */
    private Long ruleId;
    /**
     * 发货时效（0：当天；1：明天：2：后天；n：n天后）
     */
    private Integer deliveryTime;
    /**
     * 发货截止时间
     */
    private Date deliveryDeadline;

    /**
     * 指定供应商id（不指定或指定全部供应商为0）
     */
    private Long merchantCompanyId;
    /**
     * 报价方式
     */
    private String hangMethod;


}
