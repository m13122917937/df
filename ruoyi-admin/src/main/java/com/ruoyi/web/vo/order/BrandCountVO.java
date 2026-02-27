package com.ruoyi.web.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("品牌下面的订单数量")
public class BrandCountVO {


    @ApiModelProperty("品牌")
    private String brand;


    @ApiModelProperty("数量")
    private Integer sum;


}
