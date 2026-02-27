package com.ruoyi.bill.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillSUMDTO {

    /** 今日应付款 */
    private BigDecimal todayAmount;


}
