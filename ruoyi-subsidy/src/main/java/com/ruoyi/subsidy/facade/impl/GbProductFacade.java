package com.ruoyi.subsidy.facade.impl;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.subsidy.convert.GbCatalogConvert;
import com.ruoyi.subsidy.domain.GbProduct;
import com.ruoyi.subsidy.facade.IGbProductFacade;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.param.GbProductParam;
import com.ruoyi.subsidy.model.query.GbProductQuery;
import com.ruoyi.subsidy.service.GbProductService;
import com.ruoyi.subsidy.service.GbProductImageService;
import com.ruoyi.subsidy.domain.GbProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 国补商品领域实现。
 */
@Component
@RequiredArgsConstructor
public class GbProductFacade implements IGbProductFacade {

    private final GbProductService productService;
    private final GbProductImageService productImageService;

    @Override
    public PageBO<GbProductBO> page(final GbProductQuery query, final PageParamV2 pageParam) {
        PageUtils.startPage(pageParam);
        List<GbProduct> products = productService.list(DynamicCondition.toWrapper(query));
        return PageUtils.fromList(products, GbCatalogConvert.INSTANCE::toProductBOList);
    }

    @Override
    public GbProductBO getOne(final GbProductQuery query) {
        return GbCatalogConvert.INSTANCE.toProductBO(productService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public GbProductBO save(final GbProductParam param) {
        GbProduct product = GbCatalogConvert.INSTANCE.toProductEntity(param);
        return GbCatalogConvert.INSTANCE.toProductBO(productService.saveProduct(product));
    }

    @Override
    public boolean update(final Long id, final GbProductParam param) {
        GbProduct product = GbCatalogConvert.INSTANCE.toProductEntity(param).setId(id);
        return productService.updateProduct(product);
    }

    @Override
    public List<GbProductBO> listRecommended() {
        List<GbProduct> products = productService.list(DynamicCondition.toWrapper(
                new GbProductQuery().setRecommended(1).setStatus(1)));
        return GbCatalogConvert.INSTANCE.toProductBOList(products);
    }

    @Override
    public List<GbProductImage> listImages(final Long productId) {
        return productImageService.listByProductId(productId);
    }
}
