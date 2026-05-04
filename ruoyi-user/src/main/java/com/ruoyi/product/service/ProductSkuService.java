package com.ruoyi.product.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.product.convert.ProductSkuCov;
import com.ruoyi.product.model.bo.ProductSkuBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.product.mapper.ProductSkuMapper;
import com.ruoyi.product.domain.ProductSku;

/**
 * 商品基础数据Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-21
 */
@Service
public class ProductSkuService extends ServiceImpl<ProductSkuMapper, ProductSku> {
    @Autowired
    private ProductSkuMapper productSkuMapper;

    public List<String> listBrand() {
        List<ProductSku> list = this.list(new QueryWrapper<ProductSku>().select(" DISTINCT brand "));
        if (CollectionUtil.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list.stream().map(ProductSku::getBrand).collect(Collectors.toList());
    }

    public List<String> listCategory(String brand) {
        List<ProductSku> list = this.list(new QueryWrapper<ProductSku>().select(" DISTINCT category ").eq("brand", brand));
        if (CollectionUtil.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        return list.stream().map(ProductSku::getCategory).collect(Collectors.toList());
    }

    public List<ProductSkuBO> productList(String productName) {
        QueryWrapper<ProductSku> qw = new QueryWrapper<ProductSku>().select(" DISTINCT  product_name");
        if (StrUtil.isNotBlank(productName)) {
            qw = qw.like("product_name", productName);
        }
        qw.last(" limit 50 ");
        List<ProductSku> list = this.list(qw);
        return ProductSkuCov.INSTANCE.listToBO(list);
    }

}
