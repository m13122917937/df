package com.ruoyi.user.facade.impl;

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
import com.ruoyi.framework.mybatis.DynamicCondition;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthInfoFacade implements IAuthInfoFacade {

    private final AuthInfoService authInfoService;

    @Override
    public List<AuthInfoBO> queryList(AuthInfoQuery authInfoQuery) {
        List<AuthInfo> list = authInfoService.list(DynamicCondition.toWrapper(authInfoQuery));
        return AuthInfoConvert.INSTANCE.domainToBoList(list);
    }

    @Override
    public AuthInfoBO queryOne(AuthInfoQuery authInfoQuery) {
        return AuthInfoConvert.INSTANCE.domainToBo(authInfoService.getOne(DynamicCondition.toWrapper(authInfoQuery)));
    }

    @Override
    public PageBO<AuthInfoBO> pageQuery(AuthInfoQuery authInfoQuery, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<AuthInfo> list = authInfoService.list(DynamicCondition.toWrapper(authInfoQuery));
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
