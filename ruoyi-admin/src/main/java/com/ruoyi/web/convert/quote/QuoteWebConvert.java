package com.ruoyi.web.convert.quote;

import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuotePriceTierQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.web.vo.quote.QuotePriceTierQueryRequest;
import com.ruoyi.web.vo.quote.QuotePriceTierSaveRequest;
import com.ruoyi.web.vo.quote.QuotePriceTierVO;
import com.ruoyi.web.vo.quote.QuoteProductQueryRequest;
import com.ruoyi.web.vo.quote.QuoteProductSaveRequest;
import com.ruoyi.web.vo.quote.QuoteProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 报价 Web 层对象转换器。
 */
@Mapper
public interface QuoteWebConvert {

    QuoteWebConvert INSTANCE = Mappers.getMapper(QuoteWebConvert.class);

    /**
     * 转换价格档位查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuotePriceTierQuery toTierQuery(QuotePriceTierQueryRequest source);

    /**
     * 转换价格档位保存请求。
     *
     * @param source Web 保存请求
     * @return 领域参数
     */
    QuotePriceTierParam toTierParam(QuotePriceTierSaveRequest source);

    /**
     * 批量转换价格档位响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuotePriceTierVO> toTierVOList(List<QuotePriceTierBO> source);

    /**
     * 转换商品查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    QuoteProductQuery toProductQuery(QuoteProductQueryRequest source);

    /**
     * 转换商品保存请求。
     *
     * @param source Web 保存请求
     * @return 领域参数
     */
    QuoteProductParam toProductParam(QuoteProductSaveRequest source);

    /**
     * 批量转换商品响应。
     *
     * @param source 业务对象集合
     * @return Web 响应集合
     */
    List<QuoteProductVO> toProductVOList(List<QuoteProductBO> source);
}
