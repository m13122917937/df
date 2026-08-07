package com.ruoyi.quote.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuoteProductBO;
import com.ruoyi.quote.model.param.QuoteProductParam;
import com.ruoyi.quote.model.query.QuoteProductQuery;

import java.util.List;

/**
 * 报价商品领域对外接口。
 */
public interface IQuoteProductFacade {

    /**
     * 分页查询报价商品（含最新报价）。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 报价商品分页数据
     */
    PageBO<QuoteProductBO> page(QuoteProductQuery query, PageParamV2 pageParam);

    /**
     * 查询报价商品全量（含最新报价，导出用）。
     *
     * @param query 查询条件
     * @return 报价商品集合
     */
    List<QuoteProductBO> list(QuoteProductQuery query);

    /**
     * 新增或更新报价商品基础信息。
     *
     * @param param 商品参数
     */
    void save(QuoteProductParam param);

    /**
     * 删除报价商品及其价格明细。
     *
     * @param id 商品ID
     */
    void delete(Long id);
}
