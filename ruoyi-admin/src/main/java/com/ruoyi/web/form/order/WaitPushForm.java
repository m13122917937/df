package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WaitPushForm {


    @NotNull(message = "订单编号不能为空")
    private List<String> orderCodeList;


    @NotNull(message = "公司id不能为空")
    private Long companyId;


    @NotNull(message = "价格不能为空")
    private BigDecimal price;


    @NotNull(message = "发货时效不能为空")
    private Integer deliveryTime;


    @NotNull(message = "账期不能为空")
    private Integer accountingPeriod;


    @NotNull(message = "请选择推单用户")
    private Long userId;
}
