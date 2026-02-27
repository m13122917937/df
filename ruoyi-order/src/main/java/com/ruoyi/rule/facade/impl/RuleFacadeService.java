package com.ruoyi.rule.facade.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.model.page.PageParam;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.order.domain.Rule;
import com.ruoyi.order.model.param.RuleParam;
import com.ruoyi.order.model.query.RuleQuery;
import com.ruoyi.rule.convert.RuleCov;
import com.ruoyi.rule.facade.IRuleFacade;
import com.ruoyi.rule.manager.RuleManager;
import com.ruoyi.rule.model.bo.RuleBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 规则Service接口
 *
 * @author ruoyi
 * @date 2025-09-10
 */
@Service
public class RuleFacadeService implements IRuleFacade {

    @Autowired
    private RuleManager ruleManager;

    @Override
    public List<RuleBO> list(RuleQuery query) {
        Wrapper<Rule> wrapper = DynamicCondition.toWrapper(query);
        return RuleCov.INSTANCE.listToBO(ruleManager.list(wrapper));
    }

    @Override
    public PageBO<RuleBO> listPage(RuleQuery query, PageParamV2 pageParam) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
        List<Rule> list = ruleManager.list(DynamicCondition.toWrapper(query, pageParam.getSort()));
        return PageUtils.fromList(list, RuleCov.INSTANCE::listToBO);
    }

    @Override
    public RuleBO getOne(RuleQuery query) {
        return RuleCov.INSTANCE.toBO(ruleManager.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(RuleParam param, RuleQuery query) {
        return ruleManager.update(RuleCov.INSTANCE.paramToDomain(param), DynamicCondition.toWrapper(query));
    }

    @Override
    public RuleBO save(final RuleParam param) {
        Rule domain = RuleCov.INSTANCE.paramToDomain(param);
        domain.setCreateTime(DateUtil.date());
        ruleManager.save(domain);
        return RuleCov.INSTANCE.toBO(domain);
    }

//    @Override
//    public List<String> distinctPlatform(final DateTime dateTime) {
//        return ruleManager.distinctPlatform(dateTime);
//    }
//
//    @Override
//    public List<String> distinctShop(final DateTime dateTime) {
//        return ruleManager.distinctShop(dateTime);
//    }


    @Override
    public List<RuleBO> distinctSku(final DateTime dateTime) {
        List<Rule> list = ruleManager.distinctSku(dateTime);
        return RuleCov.INSTANCE.listToBO(list);
    }

    @Override
    public boolean delete(final RuleQuery ruleQuery) {
        return ruleManager.remove(DynamicCondition.toWrapper(ruleQuery));
    }

    @Override
    public List<String> brandList(DateTime dateTime) {
        return ruleManager.brandList(dateTime);
    }

    @Override
    public List<String> categoryList(DateTime dateTime) {
        return ruleManager.categoryList(dateTime);
    }

}
