package com.ruoyi.web.vo.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data

public class BillSumVO {


    private Long supplierId;


    private String supplierName;


    private String bankName;


    private String bankAccount;


    private BigDecimal billingAmount;


    private List<BillPlanVO> billPlanVOS;


}
