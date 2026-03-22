/**
 * Copyright 2025 bejson.com
 */
package com.ruoyi.web.vo.express;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)

public class RouteSubscribeVO {


    private String logisticsCompany;


    private String logisticsNo;


    private RouteInfoVO routeInfoVO;


    private List<DetailInfoVO> detailInfoVOList;


}