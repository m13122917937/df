package com.ruoyi.kuaidi100.model.e;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class EOrderConsts {


    @Getter
    @AllArgsConstructor
    public enum PrintType {

        NON("NON"),
        IMAGE("IMAGE"),
        HTML("HTML"),
        ORDERFIRST("ORDERFIRST"),
        CLOUD("CLOUD");

        private String printType;

    }

}

