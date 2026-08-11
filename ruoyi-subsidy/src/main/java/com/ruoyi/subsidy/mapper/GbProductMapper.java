package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国补商品 Mapper。
 */
@Mapper
public interface GbProductMapper extends BaseMapper<GbProduct> {

    /** 统计上架商品数。 */
    Long countOnSale();
}
