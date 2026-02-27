package com.ruoyi.web.vo.bill;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PayCompanySummaryVO {

    @ApiModelProperty("付款主体id")
    private Long payCompanyId;

    @ApiModelProperty("付款主体名称")
    private String payCompanyName;

    @ApiModelProperty("付款金额")
    private BigDecimal totalBillingAmount;

    @ApiModelProperty("付款账单集合iD")
    private List<Long> billIds;

}