package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisRefundFact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营退款事实数据访问接口。
 */
@Mapper
public interface AnalysisRefundFactMapper extends BaseMapper<AnalysisRefundFact> {
    AnalysisRefundFact selectByRefundKey(@Param("refundKey") String refundKey);

    List<AnalysisRefundFact> selectByRefundDate(@Param("refundDate") LocalDate refundDate);
}
