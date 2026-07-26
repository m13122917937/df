package com.ruoyi.order.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 定向推送供应商参数。
 */
@Data
@Accessors(chain = true)
public class SupplierPushParam {

    private List<String> orderCodeList;

    private Long companyId;

    private String companyName;

    private String companyNickName;

    private Long userId;

    private String userName;

    private String userPhone;

    private BigDecimal price;

    private Integer deliveryTime;

    private Integer accountingPeriod;

    private Long operatorId;
}
