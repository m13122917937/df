package com.ruoyi.web.vo.index;

import lombok.Data;
import lombok.experimental.Accessors;

@Data

@Accessors(chain = true)
public class IndexProductVO {


    private String productName;


    private String skuName;


    private Long quantity;
}
