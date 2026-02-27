package com.ruoyi.consts;

import com.ruoyi.biz.pay.handler.PayHandler;
import com.ruoyi.biz.pay.handler.SimulatePayHandler;
import com.ruoyi.biz.pay.handler.WxPayHandler;
import com.ruoyi.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付类型.
 *
 */
@Getter
@AllArgsConstructor
public enum PayTypes {

    /**
     * 简单.
     */
    SIMULATE(1, SimulatePayHandler.class),

    /**
     * 微信支付.
     */
    WEIXIN(2, WxPayHandler.class);

    private final int code;

    private final Class<? extends PayHandler> handler;

    public static PayTypes fromCode(final Integer code) {
        if (Objects.nonNull(code)) {
            for (PayTypes types : PayTypes.values()) {
                if (types.getCode() == code) {
                    return types;
                }
            }
        }
        throw new ServiceException("不支持的支付类型");
    }
}
