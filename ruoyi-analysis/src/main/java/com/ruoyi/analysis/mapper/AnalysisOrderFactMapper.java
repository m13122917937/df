package com.ruoyi.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.analysis.domain.AnalysisOrderFact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 经营订单事实数据访问接口。
 */
@Mapper
public interface AnalysisOrderFactMapper extends BaseMapper<AnalysisOrderFact> {
    AnalysisOrderFact selectByFactKey(@Param("factKey") String factKey);

    List<AnalysisOrderFact> selectByBusinessDate(@Param("businessDate") LocalDate businessDate);

    AnalysisOrderFact selectRefundSource(@Param("originalOrderNo") String originalOrderNo,
                                         @Param("goodsNo") String goodsNo);
}
