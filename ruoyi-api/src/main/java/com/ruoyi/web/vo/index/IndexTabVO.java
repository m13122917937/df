package com.ruoyi.web.vo.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@ApiModel("首页tab分类")
public class IndexTabVO {

    /**
     * 字典标签
     */
    @ApiModelProperty("字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @ApiModelProperty("字典键值")
    private String dictValue;


    /**
     * 订单数量
     */
    @ApiModelProperty("订单数量")
    private Long num;


}
