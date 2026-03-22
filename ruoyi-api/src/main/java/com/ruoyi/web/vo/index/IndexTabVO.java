package com.ruoyi.web.vo.index;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j

public class IndexTabVO {

    /**
     * 字典标签
     */

    private String dictLabel;

    /**
     * 字典键值
     */

    private String dictValue;


    /**
     * 订单数量
     */

    private Long num;


}
