package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.quote.service.QuoteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价商品领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuoteProductFacade implements IQuoteProductFacade {

    private final QuoteProductService quoteProductService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuoteProductBO> page(final QuoteProductQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuoteProduct> products = quoteProductService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-sort_order,-id")));
        return PageUtils.fromList(products, QuoteConvert.INSTANCE::toProductBOList);
    }

    /** {@inheritDoc} */
    @Override
    public List<QuoteProductBO> list(final QuoteProductQuery query) {
        List<QuoteProduct> products = quoteProductService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return QuoteConvert.INSTANCE.toProductBOList(products);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuoteProductParam param) {
        quoteProductService.saveProduct(param);
    }

    /** {@inheritDoc} */
    @Override
    public void savePrices(final QuoteProductParam param) {
        quoteProductService.savePrices(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quoteProductService.deleteProduct(id);
    }
}
