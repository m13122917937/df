package com.ruoyi.biz.quote;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.facade.IQuoteBrandFacade;
import com.ruoyi.quote.facade.IQuoteCategoryFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 批发报价展示应用编排服务。
 */
@Component
@RequiredArgsConstructor
public class QuoteBizService {

    private final IQuoteProductFacade quoteProductFacade;
    private final IQuoteBrandFacade quoteBrandFacade;
    private final IQuoteCategoryFacade quoteCategoryFacade;

    /**
     * 分页查询报价商品。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 报价商品分页数据
     */
    public PageBO<QuoteProductBO> pageProducts(final QuoteProductQuery query, final PageParamV2 pageParam) {
        return quoteProductFacade.page(query, pageParam);
    }

    /**
     * 查询品牌列表。
     *
     * @return 品牌列表
     */
    public List<QuoteBrandBO> listBrands() {
        return quoteBrandFacade.list(new QuoteBrandQuery());
    }

    /**
     * 查询品类列表。
     *
     * @return 品类列表
     */
    public List<QuoteCategoryBO> listCategories() {
        return quoteCategoryFacade.list(new QuoteCategoryQuery());
    }
}
