package com.ruoyi.mapper.bill;

import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.web.form.ExpressOrderForm;
import com.ruoyi.web.vo.bill.BillExportVO;
import com.ruoyi.web.vo.bill.TodayBillPayPlanVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BillConvert {

    BillConvert INSTANCE = Mappers.getMapper(BillConvert.class);


    List<TodayBillPayPlanVO> toDate(List<BillPayPlanBO> data);

    List<BillExportVO> toExcelVO(List<BillBO> list);

    BillParam toBillParam(BillBO billBO);


}
