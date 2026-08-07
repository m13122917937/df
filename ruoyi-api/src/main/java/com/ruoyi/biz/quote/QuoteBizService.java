package com.ruoyi.biz.quote;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.facade.IQuotePriceTierFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.query.QuotePriceTierQuery;
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

    private final IQuotePriceTierFacade quotePriceTierFacade;
    private final IQuoteProductFacade quoteProductFacade;

    /**
     * 查询全部价格档位。
     *
     * @return 价格档位集合
     */
    public List<QuotePriceTierBO> listTiers() {
        return quotePriceTierFacade.list(new QuotePriceTierQuery());
    }

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
    public List<String> listBrands() {
        return quoteProductFacade.listBrands();
    }

    /**
     * 查询品类列表。
     *
     * @return 品类列表
     */
    public List<String> listCategories() {
        return quoteProductFacade.listCategories();
    }
}
