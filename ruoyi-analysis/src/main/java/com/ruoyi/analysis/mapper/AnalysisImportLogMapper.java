package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisImportLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 核算配置导入审计数据访问接口。
 */
@Mapper
public interface AnalysisImportLogMapper extends BaseMapper<AnalysisImportLog> {

    /**
     * 查询最近导入记录。
     *
     * @param limit 最大条数
     * @return 导入记录
     */
    List<AnalysisImportLog> selectRecent(@Param("limit") int limit);
}
