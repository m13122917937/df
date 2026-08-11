package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 国补商品轮播图 Mapper。 */
@Mapper
public interface GbProductImageMapper extends BaseMapper<GbProductImage> {
    /** 查询商品轮播图。 */
    List<GbProductImage> selectByProductId(@Param("productId") Long productId);
}
