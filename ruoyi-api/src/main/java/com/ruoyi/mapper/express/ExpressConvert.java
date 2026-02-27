package com.ruoyi.mapper.express;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.express.model.param.RouteSubscribeParam;
import com.ruoyi.kuaidi100.model.SubscribeExpressCode;
import com.ruoyi.kuaidi100.model.SubscribeExpressParam;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.ProductBO;
import com.ruoyi.order.model.bo.SkuBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.param.TradeOrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import com.ruoyi.web.form.ExpressOrderForm;
import com.ruoyi.web.form.index.ProductForm;
import com.ruoyi.web.vo.index.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ExpressConvert {

    ExpressConvert INSTANCE = Mappers.getMapper(ExpressConvert.class);


    TradeOrderParam toParam(ExpressOrderForm expressOrderForm);

    RouteSubscribeParam toSubParam(SubscribeExpressParam subscribeExpressParam, SubscribeExpressCode subscribeExpressCode);

}
