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
     * 查询多个商品各自最新一条报价。
     *
     * @param productIds 商品ID集合
     * @return 最新报价集合
     */
    java.util.List<QuotePriceHistory> selectLatestByProductIds(@Param("productIds") java.util.List<Long> productIds);
}
