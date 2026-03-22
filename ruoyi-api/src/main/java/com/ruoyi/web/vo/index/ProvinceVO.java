package com.ruoyi.web.vo.index;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data

@Accessors(chain = true)
public class ProvinceVO {


    private Long district;


    private String name;



    private Long quantity;


    private List<CityVO> child;
}
