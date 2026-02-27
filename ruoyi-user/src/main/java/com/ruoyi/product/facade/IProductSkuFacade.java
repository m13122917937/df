package com.ruoyi.product.facade;

import java.util.List;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.product.domain.ProductSku;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.product.model.query.ProductSkuQuery;

/**
 * 商品基础数据Service接口
 *
 * @author ruoyi
 * @date 2025-09-21
 */
public interface IProductSkuFacade {

    List<ProductSkuBO> list(ProductSkuQuery query, SortBy sort);

    PageBO<ProductSkuBO> pageList(ProductSkuQuery query, PageParamV2 pageParamV2);

    ProductSkuBO getOne(ProductSkuQuery query);

    boolean update(ProductSkuParam param, ProductSkuQuery query);

    ProductSkuBO save(ProductSkuParam param);

    long count(ProductSkuQuery productSkuQuery);

}
