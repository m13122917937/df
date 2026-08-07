package com.ruoyi.quote.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuoteCategoryBO;
import com.ruoyi.quote.model.param.QuoteCategoryParam;
import com.ruoyi.quote.model.query.QuoteCategoryQuery;

import java.util.List;

/**
 * 报价品类领域对外接口。
 */
public interface IQuoteCategoryFacade {

    /**
     * 分页查询品类。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 品类分页数据
     */
    PageBO<QuoteCategoryBO> page(QuoteCategoryQuery query, PageParamV2 pageParam);

    /**
     * 查询全部品类（按排序）。
     *
     * @param query 查询条件
     * @return 品类集合
     */
    List<QuoteCategoryBO> list(QuoteCategoryQuery query);

    /**
     * 新增或更新品类。
     *
     * @param param 品类参数
     */
    void save(QuoteCategoryParam param);

    /**
     * 删除品类。
     *
     * @param id 品类ID
     */
    void delete(Long id);
}
