package com.ruoyi.subsidy.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbProduct;
import com.ruoyi.subsidy.mapper.GbProductMapper;
import org.springframework.stereotype.Service;

/**
 * 国补商品领域服务。
 */
@Service
public class GbProductService extends ServiceImpl<GbProductMapper, GbProduct> {

    /** 统计上架商品数。 */
    public Long countOnSale() {
        return baseMapper.countOnSale();
    }

    /**
     * 保存商品。
     *
     * @param product 商品实体
     * @return 保存后的商品
     */
    public GbProduct saveProduct(final GbProduct product) {
        product.setCreateTime(DateUtil.date()).setUpdateTime(DateUtil.date());
        save(product);
        return product;
    }

    /**
     * 更新商品。
     *
     * @param product 商品实体
     * @return 是否更新成功
     */
    public boolean updateProduct(final GbProduct product) {
        product.setUpdateTime(DateUtil.date());
        return updateById(product);
    }
}
