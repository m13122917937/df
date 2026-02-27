package com.ruoyi.order.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface ImeiConsts {

    @Getter
    @AllArgsConstructor
    public enum Activated {
        WAIT_QUERY(0, "待查寻"),
        ACTIVATED(1, "已激活"),
        NOT_ACTIVATED(2, "未激活"),
        MODEL_NOT_CONSISTENT(3, "型号不一致"),
        SUCCESS(4, "查询没问题"),
        NOT_EXITS(5, "串码不存在"),
        EXITS(6, "串码已经存在"),
        CANCEL(7, "串码已经撤销")
        ;
        private Integer code;
        private String name;
    }


    @Getter
    @AllArgsConstructor
    public enum PlatformImei {
        WAIT_QUERY(0, "待查寻"),
        RISK(1, "风险"),
        NORMAL(2, "正常");
        private Integer code;
        private String name;

        public static PlatformImei getByName(String name) {
            for (PlatformImei value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return null;
        }
    }


    @Getter
    @AllArgsConstructor
    enum ImeiRel {
        WAIT(1, "待处理"),
        OK(2, "已对应"),
        CANCEL(3, "已撤销");
        private Integer code;
        private String name;

    }

}
