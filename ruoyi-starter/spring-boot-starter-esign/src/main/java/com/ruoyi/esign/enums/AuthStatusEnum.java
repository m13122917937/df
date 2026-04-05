package com.ruoyi.esign.enums;

/**
 * 认证状态枚举
 *
 * @author ruoyi
 */
public enum AuthStatusEnum {

    /**
     * 认证中
     */
    PROCESSING(1, "认证中"),

    /**
     * 认证成功
     */
    SUCCESS(2, "认证成功"),

    /**
     * 认证失败
     */
    FAIL(3, "认证失败"),

    /**
     * 认证取消
     */
    CANCEL(4, "认证取消");

    private final Integer code;
    private final String desc;

    AuthStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static AuthStatusEnum fromCode(Integer code) {
        for (AuthStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
