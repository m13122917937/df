/**
 * Copyright 2025 bejson.com
 */
package com.ruoyi.web.vo.express;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel("快递信息")
public class RouteSubscribeVO {

    @ApiModelProperty("快递公司")
    private String logisticsCompany;

    @ApiModelProperty("快递单号")
    private String logisticsNo;

    @ApiModelProperty("快递信息")
    private RouteInfoVO routeInfoVO;

    @ApiModelProperty("物流信息")
    private List<DetailInfoVO> detailInfoVOList;


}