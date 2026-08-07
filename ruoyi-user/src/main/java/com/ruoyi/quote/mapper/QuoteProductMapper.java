package com.ruoyi.quote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.quote.domain.QuoteProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 报价商品数据访问接口。
 */
@Mapper
public interface QuoteProductMapper extends BaseMapper<QuoteProduct> {

    /**
     * 查询去重后的品牌列表（仅未删除商品）。
     *
     * @return 品牌列表
     */
    List<String> selectBrands();

    /**
     * 查询去重后的品类列表（仅未删除商品）。
     *
     * @return 品类列表
     */
    List<String> selectCategories();
}
