package com.ruoyi.esign.exception;

/**
 * e签宝异常
 *
 * @author ruoyi
 */
public class EsignException extends RuntimeException {

    /**
     * 错误码
     */
    private String code;

    /**
     * 错误信息
     */
    private String message;

    public EsignException(String message) {
        super(message);
        this.message = message;
    }

    public EsignException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public EsignException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
