package com.ruoyi.user.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CompanyEnum {

    @Getter
    @AllArgsConstructor
    public enum StopReasonEnum {
        /**
         * 0:.
         */
        STOP(0),
        /**
         * 1:1.信息验证
         */
        USE(1),
        /**
         * 2.打款验证
         */
      ;
        private Integer value;


    }


    @Getter
    @AllArgsConstructor
    public enum ContractAuthStatus {
        /**
         * 0:. 未认证
         */
        NO(0),
        /**
         * 1:已经认证
         */
        USE(1),

        ;
        private Integer value;


    }

    @Getter
    @AllArgsConstructor
    public enum ContractSignAuthStatus {
        /**
         * 0:未授权
         */
        NO(0),
        /**
         * 1:已授权
         */
        USE(1),

        ;
        private Integer value;
    }


}
