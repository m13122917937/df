package com.ruoyi.bill.model.query;

import com.ruoyi.framework.annotation.Operator;
import com.ruoyi.framework.annotation.QueryField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 排款计划对象 f_bill_pay_plan
 *
 * @author ruoyi
 * @date 2025-11-09
 */
@Data
@Accessors(chain = true)
public class BillPayPlanQuery {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 账单类型（1:批量采购，2:一件代发，3：接龙订单）
     */
    private Long billType;
    /**
     * 供应商ID
     */
    private Long supplierId;
    /**
     * 供应商ID
     */
    @QueryField(operator = Operator.IN, field = "supplier_id")
    private Set<Long> supplierIdSet;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 付款主体id
     */
    private Long payCompanyId;
    /**
     * 付款主体
     */
    private String payCompanyName;
    /**
     * 付款企业主体账号
     */
    private String payAccount;

    /**
     * 收款账户ID
     */
    private Long supplierBankId;
    /**
     * 排款金额
     */
    private BigDecimal payAmount;
    /**
     * 支付凭证图片集合
     */
    private String payVoucherUrls;
    /**
     * 状态（1:待付款；2:待确认；3:手工确认；4:默认确认；5:异常）
     */
    private Integer status;

    /**
     * 状态（1:待付款；2:待确认；3:手工确认；4:默认确认；5:异常）
     */
    @QueryField(operator = Operator.IN, field = "status")
    private List<Integer> statusList;
    /**
     * 异常原因
     */
    private String abnormalReason;
    /**
     * 付款时间
     */
    private Date payTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createTime;

    /** 创建时间 */
    @QueryField(operator = Operator.GT, field = "create_time")
    private Date gtCreateTime;

    /** 创建时间 */
    @QueryField(operator = Operator.LTE, field = "create_time")
    private Date lteCreateTime;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否被删除 0 未删除 1 已删除
     */
    private Long deleted;


}
