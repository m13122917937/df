package com.ruoyi.jky.param.warehouse;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WarehouseListParam {

    private Integer pageIndex;

    private Integer pageSize;

    private String code;

    private String name;

    private String gmtModifiedStart;

    private String gmtModifiedEnd;

    private Integer includeDeleteAndBlockup;

}
