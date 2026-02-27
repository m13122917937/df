package com.ruoyi.bill.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface PayerConsts {

    @Getter
    @AllArgsConstructor
    enum Activated {
        ACTIVATED(0, "激活"),
        DEPRECATE(1, "弃用"),
        ;

        private Integer code;

        private String message;
    }


    @Getter
    @AllArgsConstructor
    enum Platform{

        DY("抖店"),
        PDD("拼多多"),
        KS("快手"),
        TB("淘宝"),
        OTHER("其他"),
        ;

        private String message;
    }
}
