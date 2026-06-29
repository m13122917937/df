package com.ruoyi.web.form.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class WarehousingImportRowResult {

    private Integer rowIndex;

    private String skuCode;

    private String companyName;

    private Long quantity;

    private BigDecimal price;

    private Integer accountingPeriod;

    private String payerName;

    private String remark;

    private String productName;

    private String specName;

    private Boolean success;

    private String errorMessage;
}
