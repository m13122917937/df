package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

import java.util.Date;

/**
 * 排款计划对象 f_bill_pay_plan
 *
 * @author ruoyi
 * @date 2025-11-09
 */
@Data
@Accessors(chain = true)
public class TodayBillPayPlanVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;
    /**
     * 账单类型（1:批量采购，2:一件代发，3：接龙订单）
     */
    @ApiModelProperty("账单类型（1:批量采购，2:一件代发，）")
    private Long billType;

    /**
     * 付款主体id
     */
    private Long payCompanyId;
    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体")
    private String payCompanyName;
    /**
     * 收款账户ID
     */
    @ApiModelProperty("收款账户ID")
    private Long supplierBankId;
    /**
     * 排款金额
     */
    @ApiModelProperty("排款金额")
    private BigDecimal payAmount;
    /**
     * 支付凭证图片集合
     */
    @ApiModelProperty("支付凭证图片集合")
    private String payVoucherUrls;
    /**
     * 状态（1:待付款；2:待确认；3:手工确认；4:默认确认；5:异常）
     */
    @ApiModelProperty("状态（1:待付款；2:待确认；3:手工确认；4:默认确认；5:异常）")
    private Long status;
    /**
     * 异常原因
     */
    private String abnormalReason;
    /**
     * 付款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("付款时间")
    private Date payTime;



}
