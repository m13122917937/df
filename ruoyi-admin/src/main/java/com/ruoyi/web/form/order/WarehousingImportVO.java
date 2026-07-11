package com.ruoyi.web.form.order;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WarehousingImportVO {

    @ExcelProperty("SKU编码")
    private String skuCode;

    @ExcelProperty("供应商名称")
    private String companyName;

    @ExcelProperty("数量")
    private Long quantity;

    @ExcelProperty("单价")
    private BigDecimal price;

    @ExcelProperty("账期(天)")
    private Integer accountingPeriod;

    @ExcelProperty("付款主体名称")
    private String payerName;

    @ExcelProperty("备注")
    private String remark;
}
