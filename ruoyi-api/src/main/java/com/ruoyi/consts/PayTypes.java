package com.ruoyi.consts;

import com.ruoyi.common.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付类型枚举
 *
 * @author ruoyi
 */
@Getter
@AllArgsConstructor
public enum PayTypes {

    /**
     * 模拟支付 - 用于测试
     */
    SIMULATE(1, "模拟支付"),

    /**
     * 微信H5网页支付
     */
    WEIXIN_H5(2, "微信H5支付");

    private final int code;
    private final String description;

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
