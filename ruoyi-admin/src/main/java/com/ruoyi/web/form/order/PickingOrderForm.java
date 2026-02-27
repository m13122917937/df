package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class PickingOrderForm {

    @ApiModelProperty("数量，非sn管理需要提供数量")
    private Integer quantity;

    @ApiModelProperty("订单编号")
    private String orderCode;

    @ApiModelProperty("sn编码")
    private List<String> snList;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("仓库编码")
    private String warehouseCode;
}
