/**
 * Copyright 2025 bejson.com
 */
package com.ruoyi.web.vo.express;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("快递寄出地址")
public class FromVO {
    @ApiModelProperty("地址")
    private String name;

}