package com.ruoyi.biz.quote;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.facade.IQuotePriceTierFacade;
import com.ruoyi.quote.facade.IQuoteProductFacade;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuotePriceTierQuery;
import com.ruoyi.quote.model.query.QuoteProductQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 报价后台应用编排服务。
 */
@Component
@RequiredArgsConstructor
public class QuoteManageBizService {

    private final IQuotePriceTierFacade quotePriceTierFacade;
    private final IQuoteProductFacade quoteProductFacade;

    /**
     * 分页查询价格档位。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 价格档位分页数据
     */
    public PageBO<QuotePriceTierBO> pageTiers(final QuotePriceTierQuery query, final PageParamV2 pageParam) {
        return quotePriceTierFacade.page(query, pageParam);
    }

    /**
     * 查询全部价格档位。
     *
     * @return 价格档位集合
     */
    public List<QuotePriceTierBO> listTiers() {
        return quotePriceTierFacade.list(new QuotePriceTierQuery());
    }

    /**
     * 保存价格档位。
     *
     * @param param 价格档位参数
     */
    public void saveTier(final QuotePriceTierParam param) {
        quotePriceTierFacade.save(param);
    }

    /**
     * 删除价格档位。
     *
     * @param id 价格档位ID
     */
    public void deleteTier(final Long id) {
        quotePriceTierFacade.delete(id);
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
     * 保存报价商品及其价格。
     *
     * @param param 商品参数
     */
    public void saveProduct(final QuoteProductParam param) {
        quoteProductFacade.save(param);
    }

    /**
     * 删除报价商品。
     *
     * @param id 商品ID
     */
    public void deleteProduct(final Long id) {
        quoteProductFacade.delete(id);
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
