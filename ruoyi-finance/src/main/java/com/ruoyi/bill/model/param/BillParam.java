package com.ruoyi.bill.model.param;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 账单对象 f_bill
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Data
@Builder
@Accessors(chain = true)
public class BillParam {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
    /**
     * 供应商id
     */
    private Long supplierId;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 供应商账户ID
     */
    private Long supplierBankId;
    /**
     * 发货日期
     */
    private Date shipmentsDate;
    /**
     * 签收日期
     */
    private Date signedDate;
    /**
     * 结算日期
     */
    private LocalDate settlementDate;
    /**
     * 付款日期
     */
    private Date payDate;
    /**
     * 付款主体id
     */
    private Long payCompanyId;
    /**
     * 付款企业主体
     */
    private String payCompanyName;
    /**
     * 是否进行排款(0：未排款； 1：已排款)
     */
    private Integer payPlan;
    /**
     * 排款Id
     */
    private Long planId;
    /**
     * 默认状态（1:未付款；2:已付款；3:已收款；4:异常收款；5：异常账单；6:售后退货；7：停款；）
     * 预付款状态（1待扣款；2已扣款；3已充值；4已退款;5取消扣款）
     */
    private Integer status;
    /**
     * 账单金额
     */
    private BigDecimal billingAmount;

    /**
     * 单价
     */
    private BigDecimal tradePrice;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 账单类型（1:批量采购，2:一件代发）
     */
    private Integer billType;
    /**
     * 原始订单
     */
    private String originalOrderId;
    /**
     * 管家订单
     */
    private String gjOrderId;
    /**
     * 内部订单号
     */
    private String orderCode;
    /**
     * 通用名
     */
    private String productName;
    /**
     * 通用编码(无仓平台sku编码)
     */
    private String skuName;
    /**
     * 通用sku
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
     * 账期
     */
    private Integer accounting;
    /**
     * 备注
     */
    private String remark;
    /**
     * 删除状态
     */
    private Long deleted;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private Long updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 收款日期
     */
    private Date collectDate;
    /**
     * 0-货款账单，1-售后账单，2-扣罚账单
     */
    private Integer reversed;

    /**
     * 今日显示，0 不显示，1显示
     */
    private Integer todayShow;

}
