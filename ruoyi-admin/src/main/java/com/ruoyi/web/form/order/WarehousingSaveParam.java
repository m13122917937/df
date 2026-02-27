package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("入仓订单保存参数")
public class WarehousingSaveParam {

    @ApiModelProperty("商品编码")
    @NotBlank(message = "商品编码不能为空")
    private String skuCode;

    @ApiModelProperty("数量")
    @NotNull(message = "数量不能为空")
    private Long quantity;

    @ApiModelProperty("单价")
    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("企业id")
    @NotNull(message = "企业id不能为空")
    private Long companyId;

    @ApiModelProperty("账期， 0 现款，1 T+1")
    @NotNull(message = "账期不能为空")
    private Integer accountingPeriod;


}
