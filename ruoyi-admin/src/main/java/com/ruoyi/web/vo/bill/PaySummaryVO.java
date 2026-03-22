package com.ruoyi.web.vo.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaySummaryVO {


    private Long supplierId;


    private String supplierName;


    private String bankName;


    private String bankAccount;


    private List<PayCompanySummaryVO> payCompanySummaryVOS;

}