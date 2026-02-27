package com.ruoyi.order.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface TradeOrderConsts {


    @Getter
    @AllArgsConstructor
    enum TradeStatus {

        CONFIRMED(0, "待抢单"),

        EXPIRED(1, "待确认"),

        SUCCESS(2, "已成交"),

        TAKEN(3, "被抢走"),

        APPLY(4, "毁单"),
        ;
        private int code;

        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum TradeIndex {

        PRICE_LOWEST(1, "最低价");
        private int code;

        private String desc;

    }

}
