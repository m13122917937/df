package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisSyncLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 经营同步日志数据访问接口。
 */
@Mapper
public interface AnalysisSyncLogMapper extends BaseMapper<AnalysisSyncLog> {
    List<AnalysisSyncLog> selectRecent(@Param("limit") int limit);
}
