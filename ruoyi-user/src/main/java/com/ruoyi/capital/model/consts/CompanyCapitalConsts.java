package com.ruoyi.capital.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

public class CompanyCapitalConsts {


    public static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(40);

    @Getter
    @AllArgsConstructor
    public enum Types {
        //保证金
        DEPOSIT(1);

        private int code;
    }

    @Getter
    @AllArgsConstructor
    public enum LogTypes {
        //充值
        CHARGE(1, "充值"),
        ORDER(2, "订单"),
        CHASE(3, "追单"),
        ;;

        private Integer code;
        private String msg;

        public static LogTypes fromDesc(String stringValue) {
            for (LogTypes value : values()) {
                if (value.msg.equals(stringValue)) {
                    return value;
                }
            }
            return null;
        }

        public static LogTypes fromValue(Integer code) {
            for (LogTypes value : values()) {
                if (Objects.equals(value.code, code)) {
                    return value;
                }
            }
            return null;


        }
    }


}
