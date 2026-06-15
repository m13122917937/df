package com.ruoyi.jky.rep;

import com.ruoyi.jky.model.JkyResponse;
import com.ruoyi.jky.rep.inspect.InspectRep;
import com.ruoyi.jky.rep.logistics.LogisticsUpdateRep;
import com.ruoyi.jky.rep.sn.SnReportRep;
import com.ruoyi.jky.rep.stock.StockCreateAndStockInRep;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class JkyStockInAndDeliveryRep {

    private JkyResponse<InspectRep> inspectResponse;

    private JkyResponse<StockCreateAndStockInRep> stockInResponse;

    private JkyResponse<SnReportRep> snReportResponse;

    private JkyResponse<List<LogisticsUpdateRep>> logisticsUpdateResponse;

}
