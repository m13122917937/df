package com.ruoyi.web.vo.express;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("快递信息")
public class DetailInfoVO {

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("时间")
    private String ftime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("提示信息")
    private String context;
}
