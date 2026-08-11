package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国补 SKU Mapper。
 */
@Mapper
public interface GbProductSkuMapper extends BaseMapper<GbProductSku> {

    /**
     * 原子调整库存，允许结果为负数。
     *
     * @param skuId SKU ID
     * @param delta 变化量
     * @return 受影响行数
     */
    int adjustStock(@Param("skuId") Long skuId, @Param("delta") Integer delta);
}
