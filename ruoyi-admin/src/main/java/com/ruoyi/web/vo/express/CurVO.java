package com.ruoyi.web.vo.express;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("当前位置信息")
public class CurVO {

    @ApiModelProperty("地址")
    private String name;

}