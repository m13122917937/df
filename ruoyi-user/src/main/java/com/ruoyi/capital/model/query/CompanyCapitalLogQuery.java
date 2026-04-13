package com.ruoyi.capital.model.query;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.framework.mybatis.QueryField;
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
public class CompanyCapitalLogQuery {
    private static final long serialVersionUID = 1L;


    private Long id;

    private Long capitalId;

    private Long companyId;

    private String serviceId;

    private Long tradeId;

    private String orderNo;

    private BigDecimal outAmount;

    private BigDecimal addAmount;

    private BigDecimal subtotal;

    private BigDecimal availableAmount;

    private Date createTime;

    private Date updateTime;

    private String remark;

    private Integer type;


    @QueryField(operator = DynamicCondition.Operator.GTE, field = "create_time")
    private Date startDate;

    @QueryField(operator = DynamicCondition.Operator.LT, field = "create_time")
    private Date endDate;

}
