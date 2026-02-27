package com.ruoyi.web.vo.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@ApiModel("省")
@Accessors(chain = true)
public class ProvinceVO {

    @ApiModelProperty("省")
    private Long district;

    @ApiModelProperty("省名字")
    private String name;


    @ApiModelProperty("数量")
    private Long quantity;

    @ApiModelProperty("市")
    private List<CityVO> child;
}
