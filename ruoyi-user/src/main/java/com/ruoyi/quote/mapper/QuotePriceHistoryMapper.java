package com.ruoyi.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.QuotePriceHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 报价流水数据访问接口。
 */
@Mapper
public interface QuotePriceHistoryMapper extends BaseMapper<QuotePriceHistory> {

    /**
     * 按商品与日期幂等写入当天报价（存在则覆盖当天价格）。
     *
     * @param history 报价流水
     * @return 受影响行数
     */
    int upsertByProductAndDate(QuotePriceHistory history);

    /**
     * 查询多个商品各自最新一条报价。
     *
     * @param productIds 商品ID集合
     * @return 最新报价集合
     */
    java.util.List<QuotePriceHistory> selectLatestByProductIds(@Param("productIds") java.util.List<Long> productIds);
}
