package com.ruoyi.user.facade.impl;

import com.ruoyi.user.domain.WeComUserRelation;
import com.ruoyi.user.facade.IWeComUserRelationFacade;
import com.ruoyi.user.service.WeComUserRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 企业微信用户关联对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class WeComUserRelationFacade implements IWeComUserRelationFacade {
    private final WeComUserRelationService relationService;

    @Override
    public WeComUserRelation findByWeComUserId(String wecomUserId) {
        return relationService.findByWeComUserId(wecomUserId);
    }

    @Override
    public WeComUserRelation findByUserId(Long userId) {
        return relationService.findByUserId(userId);
    }

    @Override
    public WeComUserRelation bind(Long userId, String wecomUserId) {
        return relationService.bind(userId, wecomUserId);
    }

    @Override
    public List<WeComUserRelation> listAll() {
        return relationService.listAll();
    }

    @Override
    public void touch(WeComUserRelation relation) {
        relationService.touch(relation);
    }
}
