package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuoteCategory;
import com.ruoyi.quote.facade.IQuoteCategoryFacade;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.service.QuoteCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价品类领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuoteCategoryFacade implements IQuoteCategoryFacade {

    private final QuoteCategoryService quoteCategoryService;

    /** {@inheritDoc} */
    @Override
    public PageBO<QuoteCategoryBO> page(final QuoteCategoryQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<QuoteCategory> categories = quoteCategoryService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return PageUtils.fromList(categories, QuoteConvert.INSTANCE::toCategoryBOList);
    }

    /** {@inheritDoc} */
    @Override
    public List<QuoteCategoryBO> list(final QuoteCategoryQuery query) {
        List<QuoteCategory> categories = quoteCategoryService.list(
                DynamicCondition.toWrapper(query, SortBy.of("sort_order,id")));
        return QuoteConvert.INSTANCE.toCategoryBOList(categories);
    }

    /** {@inheritDoc} */
    @Override
    public void save(final QuoteCategoryParam param) {
        quoteCategoryService.saveCategory(param);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Long id) {
        quoteCategoryService.deleteCategory(id);
    }
}
