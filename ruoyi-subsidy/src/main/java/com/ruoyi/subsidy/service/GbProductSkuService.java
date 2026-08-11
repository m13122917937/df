package com.ruoyi.subsidy.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbStockLog;
import com.ruoyi.subsidy.domain.GbProductSku;
import com.ruoyi.subsidy.mapper.GbProductSkuMapper;
import com.ruoyi.subsidy.mapper.GbStockLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;

/**
 * 国补 SKU 领域服务。
 */
@Service
@RequiredArgsConstructor
public class GbProductSkuService extends ServiceImpl<GbProductSkuMapper, GbProductSku> {
    private final GbStockLogMapper stockLogMapper;

    /**
     * 保存 SKU。
     *
     * @param sku SKU 实体
     * @return 保存后的 SKU
     */
    public GbProductSku saveSku(final GbProductSku sku) {
        sku.setSalesQuantity(0).setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        save(sku);
        return sku;
    }

    /**
     * 更新 SKU。
     *
     * @param sku SKU 实体
     * @return 是否更新成功
     */
    public boolean updateSku(final GbProductSku sku) {
        sku.setUpdateTime(DateUtil.date());
        return updateById(sku);
    }

    /** 后台手工调整库存并记录流水。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustInventory(final Long skuId, final Integer delta, final String remark) {
        int changed = baseMapper.adjustStock(skuId, delta);
        if (changed != 1) {
            return false;
        }
        GbProductSku sku = baseMapper.selectById(skuId);
        stockLogMapper.insert(new GbStockLog().setSkuId(skuId).setChangeQuantity(delta)
                .setAfterQuantity(sku.getStockQuantity()).setChangeType("MANUAL_ADJUST")
                .setRemark(remark).setCreateTime(DateUtil.date()));
        return true;
    }
}
