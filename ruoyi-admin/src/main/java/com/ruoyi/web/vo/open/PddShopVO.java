package com.ruoyi.web.vo.open;

import com.ruoyi.common.annotation.DataScope;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PddShopVO {

    /**
     * 管家订单号
     */
    @ApiModelProperty(("管家订单号"))
    private String erpOrderId;

    /**
     * 原始订单号
     */
    @ApiModelProperty(("原始订单号"))
    private String originalOrderId;
}
