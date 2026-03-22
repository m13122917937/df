package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.List;


@Data
public class RevokeParam {


    private List<String> orderCodeList;


    private Integer revokeCode;
}
