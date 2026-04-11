package com.ruoyi.consts;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.util.Set;

public interface WebConstants {

    /**
     * 雪花算法使用，暂时写死
     */
    Snowflake snowflake = IdUtil.getSnowflake(1, 1);


    String TIMESTAMP_HEADER = "Wechatpay-Timestamp";
    String NONCE_HEADER = "Wechatpay-Nonce";
    String SINGED_HEADER = "Wechatpay-Signature";
    String SERIALNO_HEADER = "Wechatpay-Serial";


    /**
     * 支付回调地址
     */
    String PAY_NOTIFY_URL = "%s/pay/notify/%s";


    /**
     * 支付回调地址
     */
    String EXPRESS_NOTIFY_URL = "%s/express/notify/info";


    Set<String> PLATFORM_VER = Set.of("拼多多", "淘宝", "快手小店", "抖店(放心购)", "京东");

}
