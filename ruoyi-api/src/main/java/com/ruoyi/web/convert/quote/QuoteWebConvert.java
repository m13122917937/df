package com.ruoyi.web.convert.quote;

import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.web.vo.quote.QuotePriceTierVO;
import com.ruoyi.web.vo.quote.QuoteProductListRequest;
import com.ruoyi.web.vo.quote.QuoteProductListVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 批发报价页 Web 层对象转换器。
 */
@Mapper
public interface QuoteWebConvert {

    QuoteWebConvert INSTANCE = Mappers.getMapper(QuoteWebConvert.class);

    /**
     * 转换商品列表查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuoteProductQuery toProductQuery(QuoteProductListRequest source);

    /**
     * 批量转换价格档位响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuotePriceTierVO> toTierVOList(List<QuotePriceTierBO> source);

    /**
     * 批量转换商品列表响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuoteProductListVO> toProductVOList(List<QuoteProductBO> source);
}
