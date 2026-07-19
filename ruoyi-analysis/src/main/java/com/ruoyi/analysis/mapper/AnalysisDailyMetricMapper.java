package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisDailyMetric;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 每日经营指标数据访问接口。
 */
@Mapper
public interface AnalysisDailyMetricMapper extends BaseMapper<AnalysisDailyMetric> {
    int deleteByMetricDate(@Param("metricDate") LocalDate metricDate);
}
