package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisCostConfig;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 核算配置数据访问接口。
 */
@Mapper
public interface AnalysisCostConfigMapper extends BaseMapper<AnalysisCostConfig> {
    List<AnalysisCostConfig> selectEffectiveByDate(@Param("businessDate") LocalDate businessDate,
                                                   @Param("monthValue") String monthValue);

    AnalysisCostConfig selectDuplicate(AnalysisCostConfigParam param);
}
