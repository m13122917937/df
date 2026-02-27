package com.ruoyi.mapper.order;

import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.web.form.rule.RuleForm;
import com.ruoyi.web.vo.open.PddShopVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OpenConvert {

    OpenConvert INSTANCE = Mappers.getMapper(OpenConvert.class);


    List<PddShopVO> toOpens(List<OrderBO> list);
}
