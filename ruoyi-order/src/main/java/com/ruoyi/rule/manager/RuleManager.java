package com.ruoyi.rule.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.order.domain.Rule;
import com.ruoyi.rule.mapper.RuleMapper;
import com.ruoyi.rule.model.consts.RuleConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 规则Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-10
 */
@Service
public class RuleManager extends ServiceImpl<RuleMapper, Rule> {
    @Autowired
    private RuleMapper ruleMapper;


//    public List<String> distinctPlatform(final DateTime dateTime) {
//        QueryWrapper<Rule> queryWrapper = new QueryWrapper<Rule>();
//        queryWrapper.select(" DISTINCT  platform as platform ").gt("create_time", dateTime);
//        List<Rule> list = this.list(queryWrapper);
//        if (CollectionUtil.isEmpty(list)) {
//            return Collections.EMPTY_LIST;
//        }
//        return list.stream().filter(rule -> Objects.nonNull(rule) && Objects.nonNull(rule.getPlatform()) && StrUtil.isNotBlank(rule.getPlatform()))
//                .map(Rule::getPlatform).collect(Collectors.toList());
//    }


//    public List<String> distinctShop(final DateTime dateTime) {
//        QueryWrapper<Rule> queryWrapper = new QueryWrapper<Rule>();
//        queryWrapper.select(" DISTINCT  shop_name as shopName ").gt("create_time", dateTime);
//        List<Rule> list = this.list(queryWrapper);
//        if (CollectionUtil.isEmpty(list)) {
//            return Collections.EMPTY_LIST;
//        }
//        return list.stream().filter(rule -> Objects.nonNull(rule) && Objects.nonNull(rule.getShopName()) && StrUtil.isNotBlank(rule.getShopName()))
//                .map(Rule::getShopName).collect(Collectors.toList());
//    }


    public List<Rule> distinctSku(final DateTime dateTime) {
        QueryWrapper<Rule> queryWrapper = new QueryWrapper<Rule>();
        queryWrapper.select(" DISTINCT  product_name ,  sku_name , sku_code ").gt("create_time", dateTime);
        List<Rule> list = this.list(queryWrapper);
        return list;
    }

    public List<String> brandList(DateTime dateTime) {

        QueryWrapper<Rule> queryWrapper = new QueryWrapper<Rule>();
        queryWrapper.select(" DISTINCT  brand as brand ").gt("create_time", dateTime);
        List<Rule> list = this.list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list.stream().filter(rule -> Objects.nonNull(rule) && Objects.nonNull(rule.getBrand()) && StrUtil.isNotBlank(rule.getBrand()))
                .map(Rule::getBrand).collect(Collectors.toList());

    }

    public List<String> categoryList(DateTime dateTime) {
        QueryWrapper<Rule> queryWrapper = new QueryWrapper<Rule>();
        queryWrapper.select(" DISTINCT  category as category ").gt("create_time", dateTime);
        List<Rule> list = this.list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list.stream().filter(rule -> Objects.nonNull(rule) && Objects.nonNull(rule.getCategory()) && StrUtil.isNotBlank(rule.getCategory()))
                .map(Rule::getCategory).collect(Collectors.toList());
    }
}
