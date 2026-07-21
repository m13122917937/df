package com.ruoyi.product.facade;

import java.util.List;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
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

    /** @return 商品列表 */
    List<ProductSkuBO> list(ProductSkuQuery query, SortBy sort);

    /** @return 商品分页列表 */
    PageBO<ProductSkuBO> pageList(ProductSkuQuery query, PageParamV2 pageParamV2);

    /** @return 商品详情 */
    ProductSkuBO getOne(ProductSkuQuery query);

    /** @return 是否更新成功 */
    boolean update(ProductSkuParam param, ProductSkuQuery query);

    /** @return 已保存商品 */
    ProductSkuBO save(ProductSkuParam param);

    /** @return 商品数量 */
    long count(ProductSkuQuery productSkuQuery);

}
