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

    /**
     * 查询经营日期范围内的订单商品行事实。
     *
     * @param startDate 开始经营日期
     * @param endDate 结束经营日期
     * @return 订单商品行事实
     */
    List<AnalysisOrderFact> selectByBusinessDateRange(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    AnalysisOrderFact selectRefundSource(@Param("originalOrderNo") String originalOrderNo,
                                         @Param("goodsNo") String goodsNo);
}
