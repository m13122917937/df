package com.ruoyi.quote.facade.impl;

import com.ruoyi.common.model.SortBy;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.quote.convert.QuoteConvert;
import com.ruoyi.quote.domain.QuotePriceHistory;
import com.ruoyi.quote.facade.IQuotePriceHistoryFacade;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.query.QuotePriceHistoryQuery;
import com.ruoyi.quote.service.QuotePriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价流水领域对外接口实现。
 */
@Component
@RequiredArgsConstructor
public class QuotePriceHistoryFacade implements IQuotePriceHistoryFacade {

    private final QuotePriceHistoryService quotePriceHistoryService;

    /** {@inheritDoc} */
    @Override
    public void saveQuote(final QuotePriceHistoryParam param) {
        quotePriceHistoryService.saveQuote(param);
    }

    /** {@inheritDoc} */
    @Override
    public List<QuotePriceHistoryBO> list(final QuotePriceHistoryQuery query) {
        List<QuotePriceHistory> histories = quotePriceHistoryService.list(
                DynamicCondition.toWrapper(query, SortBy.of("-quote_date,-id")));
        return QuoteConvert.INSTANCE.toPriceHistoryBOList(histories);
    }
}
