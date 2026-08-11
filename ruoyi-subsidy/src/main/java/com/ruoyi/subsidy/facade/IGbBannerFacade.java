package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.domain.GbBanner;

import java.util.List;

/** 国补轮播图领域出口。 */
public interface IGbBannerFacade {
    /** 查询启用的轮播图。 */
    List<GbBanner> listEnabled();
    /** 查询全部轮播图。 */
    List<GbBanner> listAll();
    /** 保存轮播图。 */
    GbBanner save(com.ruoyi.subsidy.model.param.GbBannerParam param);
    /** 更新轮播图。 */
    boolean update(Long bannerId, com.ruoyi.subsidy.model.param.GbBannerParam param);
}
