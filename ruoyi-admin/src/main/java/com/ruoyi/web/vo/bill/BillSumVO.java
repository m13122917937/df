package com.ruoyi.web.vo.bill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("今日付款汇总")
public class BillSumVO {

    @ApiModelProperty("供应商ID")
    private Long supplierId;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行账号")
    private String bankAccount;

    @ApiModelProperty("账单金额")
    private BigDecimal billingAmount;

    @ApiModelProperty("账单列表")
    private List<BillPlanVO> billPlanVOS;


}
