package com.ruoyi.express.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface RouteSubscribeConsts {

    @Getter
    @AllArgsConstructor
    public enum Status {

        subscribe(1, "已经订阅"),

        unsubscribe(0, "未订阅");

        int value;

        String msg;
    }

}
