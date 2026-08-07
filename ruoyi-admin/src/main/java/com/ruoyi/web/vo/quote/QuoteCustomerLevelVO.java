package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 客户层级响应。
 */
@Data
public class QuoteCustomerLevelVO {

    /**
     * 客户（公司）ID
     */
    private Long companyId;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 客户层级(0-零售，1-批发1，2-批发2)
     */
    private Integer level;
}
