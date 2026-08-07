package com.ruoyi.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.QuoteProductPrice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报价商品价格明细数据访问接口。
 */
@Mapper
public interface QuoteProductPriceMapper extends BaseMapper<QuoteProductPrice> {

    /**
     * 按商品物理删除全部价格明细。
     *
     * @param productId 报价商品ID
     * @return 受影响行数
     */
    int deleteByProductId(Long productId);

    /**
     * 统计引用指定价格档位的价格明细数量。
     *
     * @param tierId 价格档位ID
     * @return 数量
     */
    long countByTierId(Long tierId);
}
