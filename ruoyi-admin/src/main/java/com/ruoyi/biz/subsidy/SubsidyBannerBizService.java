package com.ruoyi.biz.subsidy;

import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.facade.IGbBannerFacade;
import com.ruoyi.web.form.subsidy.SubsidyBannerForm;
import com.ruoyi.web.mapper.subsidy.SubsidyBannerWebConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补后台轮播图应用编排。 */
@Component
@RequiredArgsConstructor
public class SubsidyBannerBizService {
    private final IGbBannerFacade bannerFacade;
    /** 查询轮播图。 */
    public List<GbBanner> list() { return bannerFacade.listAll(); }
    /** 新增轮播图。 */
    public void save(final SubsidyBannerForm form) { bannerFacade.save(SubsidyBannerWebConvert.INSTANCE.toParam(form)); }
    /** 更新轮播图。 */
    public void update(final Long id, final SubsidyBannerForm form) { bannerFacade.update(id, SubsidyBannerWebConvert.INSTANCE.toParam(form)); }
}
