package com.ruoyi.rule.convert;

import com.ruoyi.order.domain.Rule;
import com.ruoyi.order.model.param.RuleParam;
import com.ruoyi.order.model.query.RuleQuery;
import com.ruoyi.rule.model.bo.RuleBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RuleCov {

    RuleCov INSTANCE = Mappers.getMapper(RuleCov.class);


    List<RuleBO>   listToBO(List<Rule>  list );

    RuleBO   toBO(Rule  list );

    Rule queryToDomain(RuleQuery query);

    Rule  paramToDomain(RuleParam param);
}

