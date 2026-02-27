package com.ruoyi.capital.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 企业押金对象 u_company_capital
 * 
 * @author ruoyi
 * @date 2025-09-08
 */
@Data
@Accessors(chain = true)
public class CompanyCapitalParam {
    private static final long serialVersionUID = 1L;


        private Long id;

        private Long serviceType;

        private Long companyId;

        private BigDecimal availableAmount;

        private BigDecimal frozenAmount;

        private Date createTime;

        private Date updateTime;

        private BigDecimal withdrawingAmount;


}
