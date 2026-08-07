package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuotePriceTier;
import com.ruoyi.quote.facade.IQuotePriceTierFacade;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.query.QuotePriceTierQuery;
import com.ruoyi.quote.service.QuotePriceTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价价格档位领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuotePriceTierFacade implements IQuotePriceTierFacade {

    private final QuotePriceTierService quotePriceTierService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuotePriceTierBO> page(final QuotePriceTierQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuotePriceTier> tiers = quotePriceTierService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return PageUtils.fromList(tiers, QuoteConvert.INSTANCE::toTierBOList);
    }

    /** {@inheritDoc} */
    @Override
    public List<QuotePriceTierBO> list(final QuotePriceTierQuery query) {
        List<QuotePriceTier> tiers = quotePriceTierService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return QuoteConvert.INSTANCE.toTierBOList(tiers);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuotePriceTierParam param) {
        quotePriceTierService.saveTier(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quotePriceTierService.deleteTier(id);
    }
}
