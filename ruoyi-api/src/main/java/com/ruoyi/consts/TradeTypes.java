package com.ruoyi.consts;

import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.ruoyi.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付方式.
 *
 */
@Getter
@AllArgsConstructor
public enum TradeTypes {

    QRCODE(1, TradeTypeEnum.NATIVE),
    JSAPI(2, TradeTypeEnum.JSAPI),
    MINIAPP(3, TradeTypeEnum.JSAPI);

    private final int code;

    private final TradeTypeEnum typeEnum;

    public static TradeTypes fromCode(final Integer code) {
        if (Objects.nonNull(code)) {
            for (TradeTypes types : TradeTypes.values()) {
                if (types.getCode() == code) {
                    return types;
                }
            }
        }
        throw new ServiceException("不支持的支付方式");
    }
}
