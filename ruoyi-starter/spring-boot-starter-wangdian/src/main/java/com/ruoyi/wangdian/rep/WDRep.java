package com.ruoyi.wangdian.rep;

import lombok.Data;

@Data
public class WDRep {

    private int code;

    private String message;


    public boolean isSuccess() {
        return code == 0;
    }
}
