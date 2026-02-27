package com.ruoyi.web.vo.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel("市")
@Accessors(chain = true)
public class CityVO {

    @ApiModelProperty("市")
    private Long district;

    @ApiModelProperty("数量")
    private Long quantity;

    @ApiModelProperty("市")
    private String cityName;

}
