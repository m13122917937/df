package com.ruoyi.mapper.rule;

import com.ruoyi.rule.model.param.RuleParam;
import com.ruoyi.rule.model.query.RuleQuery;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.web.form.rule.RuleForm;
import com.ruoyi.web.form.rule.RuleQueryForm;
import com.ruoyi.web.vo.order.RuleVO;
import com.ruoyi.web.vo.order.SKURuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface RuleConvert {

    RuleConvert INSTANCE = Mappers.getMapper(RuleConvert.class);

    RuleParam toParam(RuleForm ruleForm);


    List<SKURuleVO> toSKUVo(List<RuleBO> ruleBOS);

    List<RuleVO> toVOList(List<RuleBO> list);

    RuleQuery toQuery(RuleQueryForm ruleQueryForm);

}
