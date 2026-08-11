package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbBanner;
import com.ruoyi.subsidy.mapper.GbBannerMapper;
import org.springframework.stereotype.Service;
import cn.hutool.core.date.DateUtil;

import java.util.List;

/** 国补轮播图领域服务。 */
@Service
public class GbBannerService extends ServiceImpl<GbBannerMapper, GbBanner> {
    /** 查询启用的轮播图。 */
    public List<GbBanner> listEnabled() {
        return baseMapper.selectEnabled();
    }
    /** 保存轮播图。 */
    public GbBanner saveBanner(final GbBanner banner) {
        banner.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        save(banner);
        return banner;
    }
    /** 更新轮播图。 */
    public boolean updateBanner(final GbBanner banner) {
        banner.setUpdateTime(DateUtil.date());
        return updateById(banner);
    }
}
