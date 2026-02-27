package com.ruoyi.web.vo.express;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("快递信息")
public class RouteInfoVO {

    @ApiModelProperty("当前信息")
    private CurVO cur;

    @ApiModelProperty("出发地信息")
    private FromVO from;


}
