package com.ruoyi.web.vo.bill;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 排款计划对象 f_bill_pay_plan
 *
 * @author ruoyi
 * @date 2025-11-09
 */
@Data
@Accessors(chain = true)
public class BillPayPlanVO {
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

    private Long status;


}
