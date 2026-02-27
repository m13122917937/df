package com.ruoyi.kuaidi100.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订阅状态
 * @author nlsm
 */
@Getter
@AllArgsConstructor
public enum SubscribeExpressCode {

    /**
     * 监控中
     */
    SUBSCRIPTION_SUCCESS("200", "订阅成功"),

    SUBSCRIPTION_FAIL("500", "订阅失败"),

    REPEAT_SUBSCRIPTION("501", "重复订阅"),

    SIGN_FOR_NORMAL("1", "拒签转正常签收");


    private final String code;

    private final String msg;

    public static SubscribeExpressCode fromCode(String code) {
        if (StrUtil.isEmpty(code)) {
            return null;
        }
        for (SubscribeExpressCode value : SubscribeExpressCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
