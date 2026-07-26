package com.ruoyi.master.model.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售渠道保证金维护参数。
 */
@Data
public class MasterSalesChannelDepositParam {

    private Long id;
    private BigDecimal depositAmount;
}
