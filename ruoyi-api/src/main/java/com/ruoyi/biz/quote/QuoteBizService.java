package com.ruoyi.biz.quote;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.facade.IQuoteBrandFacade;
import com.ruoyi.quote.facade.IQuoteCategoryFacade;
import com.ruoyi.quote.facade.IQuotePriceHistoryFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.bo.QuotePriceHistoryBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.query.QuoteBrandQuery;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;
import com.ruoyi.quote.model.query.QuotePriceHistoryQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
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
    private final IQuotePriceHistoryFacade quotePriceHistoryFacade;
    private final ICompanyFacade companyFacade;

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

    /**
     * 查询商品历史报价（按日期倒序）。
     *
     * @param productId 商品ID
     * @return 历史报价集合
     */
    public List<QuotePriceHistoryBO> listQuoteHistory(final Long productId) {
        return quotePriceHistoryFacade.list(
                new QuotePriceHistoryQuery().setProductId(productId).setLimit(10));
    }

    /**
     * 查询客户层级，未设置时默认零售（0）。
     *
     * @param companyId 客户（公司）ID
     * @return 客户层级
     */
    public int getCustomerLevel(final Long companyId) {
        if (companyId == null) {
            return 0;
        }
        CompanyBO company = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        Integer level = company == null ? null : company.getQuoteLevel();
        return level == null ? 0 : level;
    }
}
