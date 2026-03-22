package com.ruoyi.web.vo.order;

import lombok.Data;

@Data

public class OrderExpressVO {

    /**
     * 快递公司编码
     */

    private String logisticsCode;
    /**
     * 快递单号
     */

    private String logisticsNo;



    private String phone;
}
