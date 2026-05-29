package com.ruoyi.jky.model;

import lombok.Data;

@Data
public class JkyResponse<T> {

    private Integer code;

    private String msg;

    private String subCode;

    private JkyResult<T> result;

    private String contextId;

}
