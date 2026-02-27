package com.ruoyi.bill.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface BillPayPlanConsts {

    @Getter
    @AllArgsConstructor
    enum Status {
        WAIT_PAY(1, "待付款"),
        WAIT_CONFIRM(2, "待确认"),
        USER_CONFIRM(3, "手工确认"),
        SYSTEM_CONFIRM(4, "默认确认"),
        ERROR(5, "异常");

        private Integer code;

        private String message;
    }

    @Getter
    @AllArgsConstructor
    enum PayPlan {
        NOT_PAYMENT(0, "未排款"),
        PAYMENT(1, "已排款");

        private Integer code;

        private String message;
    }

}
