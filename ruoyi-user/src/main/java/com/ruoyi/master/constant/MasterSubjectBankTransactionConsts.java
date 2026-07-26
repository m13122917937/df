package com.ruoyi.master.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 主体银行卡资金流水常量（原 TransactionsConsts）。
 *
 * @author ruoyi
 * @date 2025-12-01
 */
public interface MasterSubjectBankTransactionConsts {


    @Getter
    @AllArgsConstructor
    enum Category {
        INCOME(0, "收入"),
        EXPENSE(1, "支出"),
        ;
        private final Integer code;
        private final String name;

        public static String getName(Integer code) {
            for (Category value : values()) {
                if (value.code.equals(code)) {
                    return value.name;
                }
            }
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    enum SubCategory {

        INCOME_CHARGE(0, "平台费用"),
        OUT_PAY(0, "付款"),
        ;
        private final Integer code;
        private final String name;
    }

}
