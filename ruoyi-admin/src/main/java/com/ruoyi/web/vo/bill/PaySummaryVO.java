package com.ruoyi.web.vo.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaySummaryVO {

    @ApiModelProperty("供应商ID")
    private Long supplierId;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行账号")
    private String bankAccount;

    @ApiModelProperty("付款主体列表")
    private List<PayCompanySummaryVO> payCompanySummaryVOS;

}