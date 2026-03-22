package com.ruoyi.web.vo.bill;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PayCompanySummaryVO {


    private Long payCompanyId;


    private String payCompanyName;


    private BigDecimal totalBillingAmount;


    private List<Long> billIds;

}