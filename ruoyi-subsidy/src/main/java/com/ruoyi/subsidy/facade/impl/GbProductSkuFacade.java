package com.ruoyi.subsidy.facade.impl;

import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.subsidy.convert.GbCatalogConvert;
import com.ruoyi.subsidy.domain.GbProductSku;
import com.ruoyi.subsidy.facade.IGbProductSkuFacade;
import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.subsidy.model.param.GbProductSkuParam;
import com.ruoyi.subsidy.model.query.GbProductSkuQuery;
import com.ruoyi.subsidy.service.GbProductSkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import cn.hutool.core.lang.Assert;

import java.util.List;

/**
 * 国补 SKU 领域实现。
 */
@Component
@RequiredArgsConstructor
public class GbProductSkuFacade implements IGbProductSkuFacade {

    private final GbProductSkuService skuService;

    @Override
    public List<GbProductSkuBO> list(final GbProductSkuQuery query) {
        return GbCatalogConvert.INSTANCE.toSkuBOList(skuService.list(DynamicCondition.toWrapper(query)));
    }

    @Override
    public GbProductSkuBO getOne(final GbProductSkuQuery query) {
        return GbCatalogConvert.INSTANCE.toSkuBO(skuService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public GbProductSkuBO save(final GbProductSkuParam param) {
        GbProductSku sku = GbCatalogConvert.INSTANCE.toSkuEntity(param);
        return GbCatalogConvert.INSTANCE.toSkuBO(skuService.saveSku(sku));
    }

    @Override
    public boolean update(final Long id, final GbProductSkuParam param) {
        GbProductSku sku = GbCatalogConvert.INSTANCE.toSkuEntity(param).setId(id);
        return skuService.updateSku(sku);
    }

    @Override
    public void adjustInventory(final Long skuId, final Integer delta, final String remark) {
        Assert.notNull(skuId, "SKU不能为空");
        Assert.isTrue(delta != null && delta != 0, "调整数量不能为零");
        Assert.isTrue(skuService.adjustInventory(skuId, delta, remark), "SKU不存在");
    }
}
