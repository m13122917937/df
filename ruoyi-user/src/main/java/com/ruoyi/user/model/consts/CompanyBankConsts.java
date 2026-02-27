package com.ruoyi.user.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface CompanyBankConsts {

    @Getter
    @AllArgsConstructor
    public enum Defaulted {
        /**
         * 否
         */
        NO(0),
        /**
         * 是
         */
        YES(1),
        ;
        private int value;


    }

}
