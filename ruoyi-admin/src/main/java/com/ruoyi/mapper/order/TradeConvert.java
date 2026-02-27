package com.ruoyi.mapper.order;

import com.github.pagehelper.Page;
import com.ruoyi.order.domain.dto.OrderStatusDTO;
import com.ruoyi.order.model.bo.BrandCountBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.ProvinceCountBO;
import com.ruoyi.order.model.bo.TradePriceBO;
import com.ruoyi.order.model.param.OrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.web.form.order.OrderAddForm;
import com.ruoyi.web.form.order.OrderNewForm;
import com.ruoyi.web.vo.order.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TradeConvert {

    TradeConvert INSTANCE = Mappers.getMapper(TradeConvert.class);


    List<TradePriceVO> toTradePriceVO(List<TradePriceBO> tradePriceBOS);


}
