package com.ruoyi.web.form.order;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data

public class WarehousingSaveParam {


    @NotBlank(message = "商品编码不能为空")
    private String skuCode;


    @NotNull(message = "数量不能为空")
    private Long quantity;


    @NotNull(message = "单价不能为空")
    private BigDecimal price;


    private String remark;


    @NotNull(message = "企业id不能为空")
    private Long companyId;


    @NotNull(message = "账期不能为空")
    private Integer accountingPeriod;

    @NotNull(message = "付款主体不能为空")
    private Long payerId;


}
