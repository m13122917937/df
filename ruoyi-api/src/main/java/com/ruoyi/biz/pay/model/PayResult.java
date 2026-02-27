package com.ruoyi.biz.pay.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付通知处理结果.
 *
 * @author zenghao
 * @date 2022/5/30
 */
@Data
@Accessors(chain = true)
public class PayResult implements Serializable {

    /**
     * 订单支付状态.
     */
    private Boolean tradeState;

    /**
     * 第三方支付单号.
     */
    private String thirdTradeNo;

    /**
     * 通知回调响应给第三方的数据.
     */
    private String thirdData;

    /**
     * 第三方响应的订单支付金额.
     */
    private BigDecimal amount;

    public static PayResult failure() {
        PayResult result = new PayResult();
        result.setTradeState(false);
        return result;
    }
}
