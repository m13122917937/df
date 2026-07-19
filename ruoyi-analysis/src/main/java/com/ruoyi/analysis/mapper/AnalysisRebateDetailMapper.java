package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisRebateDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 返利价保明细数据访问接口。
 */
@Mapper
public interface AnalysisRebateDetailMapper extends BaseMapper<AnalysisRebateDetail> {
    List<AnalysisRebateDetail> selectByActivityId(@Param("activityId") Long activityId);

    int deleteByActivityId(@Param("activityId") Long activityId);
}
