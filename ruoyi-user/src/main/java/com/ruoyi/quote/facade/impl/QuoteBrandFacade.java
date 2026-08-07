package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteBrand;
import com.ruoyi.quote.facade.IQuoteBrandFacade;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.service.QuoteBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价品牌领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuoteBrandFacade implements IQuoteBrandFacade {

    private final QuoteBrandService quoteBrandService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuoteBrandBO> page(final QuoteBrandQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuoteBrand> brands = quoteBrandService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return PageUtils.fromList(brands, QuoteConvert.INSTANCE::toBrandBOList);
    }

    /** {@inheritDoc} */
    @Override
    public List<QuoteBrandBO> list(final QuoteBrandQuery query) {
        List<QuoteBrand> brands = quoteBrandService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return QuoteConvert.INSTANCE.toBrandBOList(brands);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuoteBrandParam param) {
        quoteBrandService.saveBrand(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quoteBrandService.deleteBrand(id);
    }
}
