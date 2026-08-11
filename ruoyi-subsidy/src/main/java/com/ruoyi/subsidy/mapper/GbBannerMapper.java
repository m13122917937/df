package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbBanner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 国补轮播图 Mapper。
 */
@Mapper
public interface GbBannerMapper extends BaseMapper<GbBanner> {
    /** 查询启用的轮播图。 */
    List<GbBanner> selectEnabled();
}
