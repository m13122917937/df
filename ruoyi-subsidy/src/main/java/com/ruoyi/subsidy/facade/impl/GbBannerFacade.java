package com.ruoyi.subsidy.facade.impl;

import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.facade.IGbBannerFacade;
import com.ruoyi.subsidy.service.GbBannerService;
import com.ruoyi.subsidy.model.param.GbBannerParam;
import com.ruoyi.subsidy.convert.GbBannerConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补轮播图领域实现。 */
@Component
@RequiredArgsConstructor
public class GbBannerFacade implements IGbBannerFacade {
    private final GbBannerService bannerService;

    @Override
    public List<GbBanner> listEnabled() {
        return bannerService.listEnabled();
    }

    @Override
    public List<GbBanner> listAll() {
        return bannerService.list();
    }

    @Override
    public GbBanner save(final GbBannerParam param) {
        return bannerService.saveBanner(GbBannerConvert.INSTANCE.toEntity(param));
    }

    @Override
    public boolean update(final Long bannerId, final GbBannerParam param) {
        return bannerService.updateBanner(GbBannerConvert.INSTANCE.toEntity(param).setId(bannerId));
    }
}
