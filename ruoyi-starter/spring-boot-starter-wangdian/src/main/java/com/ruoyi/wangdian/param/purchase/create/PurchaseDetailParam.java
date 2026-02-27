package com.ruoyi.wangdian.param.purchase.create;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailParam {

    /**
     * 商家编码
     */
    private String spec_no;

    /**
     * 采购数量
     */
    private Integer num;

    /**
     * 税后单价
     */
    private BigDecimal tax_price;


}