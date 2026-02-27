package com.ruoyi.bill.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface DeductionConsts {


    @Getter
    @AllArgsConstructor
    enum Status {
        DEDUCTION(0, "已扣罚"),
        REVOKE(1, "已撤销"),
    ;

        private Integer code;

        private String message;
    }
}
