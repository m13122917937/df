package com.ruoyi.product.service;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.product.convert.ProductSkuCov;
import com.ruoyi.product.model.bo.ProductSkuBO;
import org.springframework.stereotype.Service;

import com.ruoyi.product.mapper.ProductSkuMapper;
import com.ruoyi.product.domain.ProductSku;
import com.ruoyi.product.model.query.ProductSkuQuery;

/**
 * 商品基础数据Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-21
 */
@Service
public class ProductSkuService extends ServiceImpl<ProductSkuMapper, ProductSku> {

    /**
     * 查询已维护的品牌。
     *
     * @return 品牌列表
     */
    public List<String> listBrand() {
        List<ProductSku> list = list(DynamicCondition.toWrapper(new ProductSkuQuery().setBrandGroup("brand")));
        if (CollectionUtil.isEmpty(list)) {
            return List.of();
        }
        return list.stream().map(ProductSku::getBrand).collect(Collectors.toList());
    }

    /**
     * 按品牌查询品类。
     *
     * @param brand 品牌名称
     * @return 品类列表
     */
    public List<String> listCategory(String brand) {
        ProductSkuQuery query = new ProductSkuQuery().setBrand(brand).setCategoryGroup("category");
        List<ProductSku> list = list(DynamicCondition.toWrapper(query));
        if (CollectionUtil.isEmpty(list)) {
            return List.of();
        }
        return list.stream().map(ProductSku::getCategory).collect(Collectors.toList());
    }

    /**
     * 按商品名称查询候选商品。
     *
     * @param productName 商品名称关键字
     * @return 商品列表
     */
    public List<ProductSkuBO> productList(String productName) {
        ProductSkuQuery query = new ProductSkuQuery().setProductNameLike(productName)
                .setProductNameGroup("product_name").setLimit(50);
        List<ProductSku> list = list(DynamicCondition.toWrapper(query));
        return ProductSkuCov.INSTANCE.listToBO(list);
    }

}
