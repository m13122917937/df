package com.ruoyi.user.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.user.convert.AuthInfoConvert;
import com.ruoyi.user.domain.AuthInfo;
import com.ruoyi.user.facade.IAuthInfoFacade;
import com.ruoyi.user.service.AuthInfoService;
import com.ruoyi.user.model.bo.AuthInfoBO;
import com.ruoyi.user.model.param.AuthInfoParam;
import com.ruoyi.user.model.query.AuthInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 授权信息Facade实现
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Slf4j
@Service
public class AuthInfoFacade implements IAuthInfoFacade {

    @Autowired
    AuthInfoService authInfoService;

    @Override
    public List<AuthInfoBO> queryList(AuthInfoQuery authInfoQuery) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.queryToDomain(authInfoQuery);
        List<AuthInfo> list = authInfoService.list(new QueryWrapper<>(authInfo));
        return AuthInfoConvert.INSTANCE.domainToBoList(list);
    }

    @Override
    public AuthInfoBO queryOne(AuthInfoQuery authInfoQuery) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.queryToDomain(authInfoQuery);
        return AuthInfoConvert.INSTANCE.domainToBo(authInfoService.getOne(new QueryWrapper<>(authInfo)));
    }

    @Override
    public PageBO<AuthInfoBO> pageQuery(AuthInfoQuery authInfoQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.queryToDomain(authInfoQuery);
        List<AuthInfo> list = authInfoService.list(new QueryWrapper<>(authInfo));
        return PageUtils.fromList(list, AuthInfoConvert.INSTANCE::domainToBoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthInfoBO insert(AuthInfoParam authInfoParam) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.paramToDomain(authInfoParam);
        authInfo.setCreateTime(DateUtil.date());
        authInfoService.save(authInfo);
        return AuthInfoConvert.INSTANCE.domainToBo(authInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(AuthInfoParam authInfoParam, AuthInfoQuery query) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.paramToDomain(authInfoParam);
        authInfo.setUpdateTime(DateUtil.date());
        return authInfoService.update(authInfo, DynamicCondition.toWrapper(query));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Integer id) {
        return authInfoService.removeById(id);
    }

}
