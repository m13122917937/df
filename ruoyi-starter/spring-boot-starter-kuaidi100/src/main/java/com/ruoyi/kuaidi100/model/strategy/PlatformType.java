package com.ruoyi.kuaidi100.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电商平台类型枚举
 *
 * @author kuaidi100
 */
@Getter
@AllArgsConstructor
public enum PlatformType {

    /**
     * 拼多多
     */
    PINDUODUO("pinduoduoWx", "拼多多"),

    /**
     * 抖音
     */
    DOUYIN("douyin", "抖音"),

    /**
     * 快手
     */
    KUAISHOU("kuaishou", "快手"),

    /**
     * 菜鸟淘宝
     */
    TAOBAO("taobao", "菜鸟淘宝");

    /**
     * 平台编码（用于快递100接口）
     */
    private final String code;

    /**
     * 平台名称
     */
    private final String name;

    /**
     * 根据编码获取平台类型
     *
     * @param code 平台编码
     * @return 平台类型
     */
    public static PlatformType fromCode(String code) {
        for (PlatformType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的平台编码: " + code);
    }
}