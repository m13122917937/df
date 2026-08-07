package com.ruoyi.quote.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuoteBrandBO;
import com.ruoyi.quote.model.param.QuoteBrandParam;
import com.ruoyi.quote.model.query.QuoteBrandQuery;

import java.util.List;

/**
 * 报价品牌领域对外接口。
 */
public interface IQuoteBrandFacade {

    /**
     * 分页查询品牌。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 品牌分页数据
     */
    PageBO<QuoteBrandBO> page(QuoteBrandQuery query, PageParamV2 pageParam);

    /**
     * 查询全部品牌（按排序）。
     *
     * @param query 查询条件
     * @return 品牌集合
     */
    List<QuoteBrandBO> list(QuoteBrandQuery query);

    /**
     * 新增或更新品牌。
     *
     * @param param 品牌参数
     */
    void save(QuoteBrandParam param);

    /**
     * 删除品牌。
     *
     * @param id 品牌ID
     */
    void delete(Long id);
}
