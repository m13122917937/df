package com.ruoyi.system.model.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class DictDistrictConsts {


    @Getter
    @AllArgsConstructor
    public enum Level {
        PROVINCE(1),
        CITY(2),
        DISTRICT(3);

        private int value;
    }

}
