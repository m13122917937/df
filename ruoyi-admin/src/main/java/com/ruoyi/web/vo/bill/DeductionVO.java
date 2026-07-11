package com.ruoyi.web.vo.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data

public class DeductionVO {


    /**
     * 供应商id
     */

    private Long companyId;

    /**
     * 供应商名称
     */

    private String companyName;

    /**
     * 订单号
     */

    private String orderCode;
    /**
     * 吉客云id
     */

    private String erpId;
    /**
     * 商家单号
     */

    private String originalOrderId;
    /**
     * 品牌
     */

    private String brand;
    /**
     * 品类
     */

    private String category;
    /**
     * 商品名
     */
    private String spuName;
    /**
     *
     *
     */
    private String skuName;


    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date sendTime;
    /**
     * 订单金额
     */

    private BigDecimal tradePrice;
    /**
     * 扣罚金额
     */

    private BigDecimal amount;
    /**
     * 错误原因
     */

    private String remark;

    /**
     * 状态 ， 0 已经扣罚 ， 1 已经撤销
     */

    private Integer status;
}
