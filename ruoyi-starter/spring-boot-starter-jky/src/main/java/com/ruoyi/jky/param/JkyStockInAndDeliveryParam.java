package com.ruoyi.jky.param;

import com.ruoyi.jky.param.logistics.LogisticsUpdateParam;
import com.ruoyi.jky.param.sn.SnReportParam;
import com.ruoyi.jky.param.stock.StockCreateAndStockInParam;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JkyStockInAndDeliveryParam {

    private StockCreateAndStockInParam stockInParam;

    private SnReportParam snReportParam;

    private LogisticsUpdateParam logisticsUpdateParam;

}
