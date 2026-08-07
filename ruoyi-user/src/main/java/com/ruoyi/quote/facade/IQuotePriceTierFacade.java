package com.ruoyi.quote.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.quote.model.bo.QuotePriceTierBO;
import com.ruoyi.quote.model.param.QuotePriceTierParam;
import com.ruoyi.quote.model.query.QuotePriceTierQuery;

import java.util.List;

/**
 * 报价价格档位领域对外接口。
 */
public interface IQuotePriceTierFacade {

    /**
     * 分页查询价格档位。
     *
     * @param query     查询条件
     * @param pageParam 分页参数
     * @return 价格档位分页数据
     */
    PageBO<QuotePriceTierBO> page(QuotePriceTierQuery query, PageParamV2 pageParam);

    /**
     * 查询全部价格档位（按排序）。
     *
     * @param query 查询条件
     * @return 价格档位集合
     */
    List<QuotePriceTierBO> list(QuotePriceTierQuery query);

    /**
     * 新增或更新价格档位。
     *
     * @param param 价格档位参数
     */
    void save(QuotePriceTierParam param);

    /**
     * 删除价格档位。
     *
     * @param id 价格档位ID
     */
    void delete(Long id);
}
