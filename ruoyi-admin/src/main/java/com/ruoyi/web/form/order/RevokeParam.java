package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.util.List;

@ApiOperation("撤销订单")
@Data
public class RevokeParam {

    @ApiModelProperty(value = "订单号", example = "[\"2223\", \"weqwer\"]")
    private List<String> orderCodeList;

    @ApiModelProperty("追单原因. 0，店铺后台急速退款； 1 顾客拒签/拒收； 2 派送未联系到顾客，退回， 3 24小时物流无揽收信息; 4 供应商私自拦截 5 已经从其他渠道发货")
    private Integer revokeCode;
}
