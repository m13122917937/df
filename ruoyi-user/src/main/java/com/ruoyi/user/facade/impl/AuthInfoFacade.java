package com.ruoyi.user.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.user.convert.AuthInfoConvert;
import com.ruoyi.user.domain.AuthInfo;
import com.ruoyi.user.facade.IAuthInfoFacade;
import com.ruoyi.user.service.AuthInfoService;
import com.ruoyi.user.model.bo.AuthInfoBO;
import com.ruoyi.user.model.param.AuthInfoParam;
import com.ruoyi.user.model.query.AuthInfoQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 授权信息Facade实现
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Slf4j
@Component
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
    public AuthInfoBO insert(AuthInfoParam authInfoParam) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.paramToDomain(authInfoParam);
        return AuthInfoConvert.INSTANCE.domainToBo(authInfoService.insert(authInfo));
    }

    @Override
    public boolean update(AuthInfoParam authInfoParam, AuthInfoQuery query) {
        AuthInfo authInfo = AuthInfoConvert.INSTANCE.paramToDomain(authInfoParam);
        return authInfoService.update(authInfo, query);
    }

    @Override
    public boolean deleteById(Integer id) {
        return authInfoService.deleteById(id);
    }

}
