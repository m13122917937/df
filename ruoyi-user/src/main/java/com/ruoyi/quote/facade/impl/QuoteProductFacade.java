package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.quote.service.QuotePriceHistoryService;
import com.ruoyi.quote.service.QuoteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 报价商品领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuoteProductFacade implements IQuoteProductFacade {

    private final QuoteProductService quoteProductService;
    private final QuotePriceHistoryService quotePriceHistoryService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuoteProductBO> page(final QuoteProductQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuoteProduct> products = quoteProductService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-sort_order,-id")));
        PageBO<QuoteProductBO> pageBO = PageUtils.fromList(products, QuoteConvert.INSTANCE::toProductBOList);
        attachLatestQuotes(pageBO.getData());
        return pageBO;
    }

    /** {@inheritDoc} */
    @Override
    public List<QuoteProductBO> list(final QuoteProductQuery query) {
        List<QuoteProduct> products = quoteProductService.list(
                DynamicCondition.toWrapper(query, SortBy.of("brand,product_name,spec_name,id")));
        List<QuoteProductBO> productBOS = QuoteConvert.INSTANCE.toProductBOList(products);
        attachLatestQuotes(productBOS);
        return productBOS;
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuoteProductParam param) {
        quoteProductService.saveProduct(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quoteProductService.deleteProduct(id);
    }

    private void attachLatestQuotes(final List<QuoteProductBO> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = products.stream().map(QuoteProductBO::getId).collect(Collectors.toList());
        List<QuotePriceHistory> latestQuotes = quotePriceHistoryService.listLatestByProductIds(productIds);
        Map<Long, QuotePriceHistoryBO> quoteMap = latestQuotes.stream()
                .map(QuoteConvert.INSTANCE::toPriceHistoryBO)
                .collect(Collectors.toMap(QuotePriceHistoryBO::getProductId, Function.identity(), (a, b) -> a));
        for (QuoteProductBO product : products) {
            product.setLatestQuote(quoteMap.get(product.getId()));
        }
    }
}
