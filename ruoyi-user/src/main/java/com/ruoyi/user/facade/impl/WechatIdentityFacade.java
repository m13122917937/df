package com.ruoyi.user.facade.impl;

import com.ruoyi.user.facade.IWechatIdentityFacade;
import com.ruoyi.user.model.bo.WechatIdentityBO;
import com.ruoyi.user.model.param.WechatIdentityParam;
import com.ruoyi.user.service.MemberWechatIdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.ruoyi.user.domain.MemberWechatIdentityConflict;
import java.util.List;

/**
 * 微信统一身份领域实现。
 */
@Component
@RequiredArgsConstructor
public class WechatIdentityFacade implements IWechatIdentityFacade {
    private final MemberWechatIdentityService identityService;

    @Override
    public WechatIdentityBO resolve(final WechatIdentityParam param) {
        return identityService.resolve(param);
    }

    @Override
    public String getOpenId(final Long memberId, final String channel, final String appId) {
        return identityService.getOpenId(memberId, channel, appId);
    }

    @Override
    public boolean isPurchaseAllowed(final Long memberId, final String channel, final String appId) {
        return identityService.isPurchaseAllowed(memberId, channel, appId);
    }

    @Override
    public List<MemberWechatIdentityConflict> listConflicts(final String status) {
        return identityService.listConflicts(status);
    }
}
