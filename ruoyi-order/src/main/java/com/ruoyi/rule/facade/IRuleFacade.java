package com.ruoyi.rule.facade;

import cn.hutool.core.date.DateTime;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.page.PageParam;
import com.ruoyi.rule.model.param.RuleParam;
import com.ruoyi.rule.model.query.RuleQuery;
import com.ruoyi.rule.model.bo.RuleBO;

import java.util.List;

/**
 * 规则Service接口
 *
 * @author ruoyi
 * @date 2025-09-10
 */
public interface IRuleFacade {

    List<RuleBO> list(RuleQuery query);

    PageBO<RuleBO> listPage(RuleQuery query, PageParamV2 pageParamV2);

    RuleBO getOne(RuleQuery query);

    boolean update(RuleParam param, RuleQuery query);

    RuleBO save(RuleParam param);


    List<RuleBO> distinctSku(DateTime dateTime);

    boolean delete(RuleQuery ruleQuery);

    List<String> brandList(DateTime dateTime);

    List<String> categoryList(DateTime dateTime);

}
