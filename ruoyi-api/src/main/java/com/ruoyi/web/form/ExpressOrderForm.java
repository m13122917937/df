package com.ruoyi.web.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class ExpressOrderForm {


    private String orderCode;


    /**
     * 快递单号
     */

    private String trackingNumber;

    /**
     * 快递公司
     */

    private String trackingCompany;

    /**
     * 快递公司
     */

    private String trackingCompanyCode;

    /**
     * 手机号码
     */

    private String cellphone;
}
