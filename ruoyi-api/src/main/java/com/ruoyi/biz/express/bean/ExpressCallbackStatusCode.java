//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ruoyi.biz.express.bean;


import cn.hutool.core.util.StrUtil;

public enum ExpressCallbackStatusCode {
    POLLING_STATUS("polling", "监控中"),
    SHUTDOWN_STATUS("shutdown", "结束"),
    UPDATEALL_STATUS("updateall", "重新推送"),
    ABORT_STATUS("abort", "中止");

    private final String code;
    private final String msg;

    public static ExpressCallbackStatusCode fromCode(String code) {
        if (StrUtil.isNotBlank(code)) {
            for(ExpressCallbackStatusCode value : values()) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
        }

        return POLLING_STATUS;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    private ExpressCallbackStatusCode(final String code, final String msg) {
        this.code = code;
        this.msg = msg;
    }
}
