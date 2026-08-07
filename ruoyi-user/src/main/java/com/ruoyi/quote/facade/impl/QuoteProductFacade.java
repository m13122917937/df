package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteProduct;
import com.ruoyi.quote.domain.QuoteProductPrice;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.bo.QuoteProductPriceBO;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteProductPriceQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.quote.service.QuoteProductPriceService;
import com.ruoyi.quote.service.QuoteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报价商品领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuoteProductFacade implements IQuoteProductFacade {

    private final QuoteProductService quoteProductService;
    private final QuoteProductPriceService quoteProductPriceService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuoteProductBO> page(final QuoteProductQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuoteProduct> products = quoteProductService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-sort_order,-id")));
        PageBO<QuoteProductBO> pageBO = PageUtils.fromList(products, QuoteConvert.INSTANCE::toProductBOList);
        attachPrices(pageBO.getData());
        return pageBO;
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuoteProductParam param) {
        quoteProductService.saveWithPrices(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quoteProductService.deleteWithPrices(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> listBrands() {
        return quoteProductService.listBrands();
    }

    /** {@inheritDoc} */
    @Override
    public List<String> listCategories() {
        return quoteProductService.listCategories();
    }

    private void attachPrices(final List<QuoteProductBO> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        List<Long> productIds = products.stream().map(QuoteProductBO::getId).collect(Collectors.toList());
        List<QuoteProductPrice> prices = quoteProductPriceService.list(
                DynamicCondition.toWrapper(new QuoteProductPriceQuery().setProductIds(productIds)));
        Map<Long, List<QuoteProductPriceBO>> priceMap = new HashMap<>();
        for (QuoteProductPrice price : prices) {
            priceMap.computeIfAbsent(price.getProductId(), key -> new ArrayList<>())
                    .add(QuoteConvert.INSTANCE.toPriceBO(price));
        }
        for (QuoteProductBO product : products) {
            product.setPrices(priceMap.getOrDefault(product.getId(), List.of()));
        }
    }
}
