package com.ruoyi.web.vo.quote;

import lombok.Data;

/**
 * 客户层级保存请求。
 */
@Data
public class QuoteCustomerLevelSaveRequest {

    /**
     * 客户（公司）ID
     */
    private Long companyId;

    /**
     * 客户层级(0-零售，1-批发1，2-批发2)
     */
    private Integer level;
}
