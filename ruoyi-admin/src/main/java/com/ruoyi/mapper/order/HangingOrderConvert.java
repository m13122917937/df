package com.ruoyi.mapper.order;

import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.web.form.rule.RuleForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HangingOrderConvert {

    HangingOrderConvert INSTANCE = Mappers.getMapper(HangingOrderConvert.class);

    HangingOrderParam toParam(RuleForm ruleParam);


    HangingOrderParam ruleToHangingParam(RuleBO ruleBO);

}
