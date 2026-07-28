package com.ruoyi.biz.master;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 商品主数据应用编排服务。
 */
@Component
@RequiredArgsConstructor
public class MasterProductBizService {

    private final IProductSkuFacade productSkuFacade;

    /**
     * 分页查询商品 SKU 主数据。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 商品 SKU 分页数据
     */
    public PageBO<ProductSkuBO> page(final ProductSkuQuery query, final PageParamV2 pageParam) {
        return productSkuFacade.pageList(query, pageParam);
    }
}
