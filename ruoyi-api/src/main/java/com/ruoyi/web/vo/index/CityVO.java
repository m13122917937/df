package com.ruoyi.web.vo.index;

import lombok.Data;
import lombok.experimental.Accessors;

@Data

@Accessors(chain = true)
public class CityVO {


    private Long district;


    private Long quantity;


    private String cityName;

}
