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


}
