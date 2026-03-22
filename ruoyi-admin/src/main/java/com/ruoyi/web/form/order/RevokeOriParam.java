package com.ruoyi.web.form.order;

import lombok.Data;

import java.util.List;


@Data
public class RevokeOriParam {


    private List<String> originalOrderIdList;


    private Integer revokeCode;
}
