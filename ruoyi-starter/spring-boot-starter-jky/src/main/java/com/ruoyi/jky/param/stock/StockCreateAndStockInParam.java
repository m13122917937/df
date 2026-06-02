package com.ruoyi.jky.param.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StockCreateAndStockInParam {

    @JsonProperty("warehouse_code")
    private String warehouseCode;

    @JsonProperty("goods_code")
    private String goodsCode;

    private Integer quantity;

    @JsonProperty("batch_no")
    private String batchNo;

}
