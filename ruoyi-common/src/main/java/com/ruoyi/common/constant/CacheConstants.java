package com.ruoyi.common.constant;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "fy:admin:token:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "fy:admin:captcha:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "fy:admin:cfg:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "fy:admin:dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "fy:admin:rpt:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "fy:admin:limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "fy:admin:pwd:";
}
