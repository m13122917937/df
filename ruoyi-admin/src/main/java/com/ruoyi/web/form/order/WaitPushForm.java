package com.ruoyi.web.form.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WaitPushForm {

    @ApiModelProperty("订单编号列表")
    @NotNull(message = "订单编号不能为空")
    private List<String> orderCodeList;

    @ApiModelProperty("公司id")
    @NotNull(message = "公司id不能为空")
    private Long companyId;

    @ApiModelProperty("价格")
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @ApiModelProperty("发货时效（0：当天；1：明天：2：后天；n：n天后）")
    @NotNull(message = "发货时效不能为空")
    private Integer deliveryTime;

    @ApiModelProperty("账期， 0 T+0，1 T+1")
    @NotNull(message = "账期不能为空")
    private Integer accountingPeriod;

    @ApiModelProperty("用户Id")
    @NotNull(message = "请选择推单用户")
    private Long userId;
}
