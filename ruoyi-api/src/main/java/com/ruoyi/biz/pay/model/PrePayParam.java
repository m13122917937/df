package com.ruoyi.biz.pay.model;

import com.ruoyi.consts.PayTypes;
import com.ruoyi.consts.TradeTypes;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预支付参数.
 *
 */
@Data
@Accessors(chain = true)
public class PrePayParam implements Serializable {

    /**
     * 支付类型
     */
    private PayTypes payType;

    /**
     * 支付方式
     */
    private TradeTypes tradeType;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 通知回调url.
     */
    private String notifyUrl;

    /**
     * 微信支付参数
     */
    private WxPayParam wxPayParam;

    @Data
    @Accessors(chain = true)
    public static class WxPayParam implements Serializable {

        /**
         * 微信公众号appid.目前使用配置中的appid
         */
        private String appid;

        /**
         * 支付终端ip.
         */
        private String clientIp;

        /**
         * 支付用户的openid
         */
        private String openid;
    }

}
