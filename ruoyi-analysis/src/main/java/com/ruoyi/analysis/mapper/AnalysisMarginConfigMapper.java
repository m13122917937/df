package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisMarginConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 保证金配置数据访问接口。
 */
@Mapper
public interface AnalysisMarginConfigMapper extends BaseMapper<AnalysisMarginConfig> {

    /**
     * 按平台和店铺查询保证金配置。
     *
     * @param platform 平台
     * @param shopName 店铺名称
     * @return 保证金配置
     */
    AnalysisMarginConfig selectByPlatformAndShop(@Param("platform") String platform,
                                                 @Param("shopName") String shopName);
}
