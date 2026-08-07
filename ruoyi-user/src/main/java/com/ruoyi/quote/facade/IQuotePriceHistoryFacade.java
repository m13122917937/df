package com.ruoyi.quote.facade;

import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.param.QuotePriceHistoryParam;
import com.ruoyi.quote.model.query.QuotePriceHistoryQuery;

import java.util.List;

/**
 * 报价流水领域对外接口。
 */
public interface IQuotePriceHistoryFacade {

    /**
     * 保存当天报价。
     *
     * @param param 报价流水参数
     */
    void saveQuote(QuotePriceHistoryParam param);

    /**
     * 查询报价流水（历史报价，按日期倒序）。
     *
     * @param query 查询条件
     * @return 报价流水集合
     */
    List<QuotePriceHistoryBO> list(QuotePriceHistoryQuery query);
}
