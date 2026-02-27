package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillPlanVO {


    /**
     * 主键
     */
    private Long id;

    /**
     * 付款主体id
     */
    @ApiModelProperty("付款主体id")
    private Long payCompanyId;
    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体")
    private String payCompanyName;


    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体")
    private String payCompanyBank;


    /**
     * 付款主体
     */
    @ApiModelProperty("付款主体")
    private String payCompanyBankAccount;


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
     * 付款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("付款时间")
    private Date payTime;


}
