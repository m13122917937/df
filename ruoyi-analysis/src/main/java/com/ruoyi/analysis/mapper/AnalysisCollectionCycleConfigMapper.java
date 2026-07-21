package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisCollectionCycleConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** 回款周期数据访问接口。 */
@Mapper
public interface AnalysisCollectionCycleConfigMapper extends BaseMapper<AnalysisCollectionCycleConfig> {
    AnalysisCollectionCycleConfig selectByPlatformAndShop(@Param("platform") String platform,
                                                           @Param("shopName") String shopName);
}
