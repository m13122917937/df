package com.ruoyi.capital.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业押金记录对象 u_company_capital_log
 *
 * @author ruoyi
 * @date 2025-09-08
 */
@Data
@Accessors(chain = true)
public class CompanyCapitalLogParam {
    private static final long serialVersionUID = 1L;


    private Long id;

    private Long capitalId;

    private Long companyId;

    private String serviceId;

    private String orderNo;

    private Long tradeId;

    private BigDecimal outAmount;

    private BigDecimal addAmount;

    private BigDecimal subtotal;

    private BigDecimal availableAmount;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private Integer type;


}
