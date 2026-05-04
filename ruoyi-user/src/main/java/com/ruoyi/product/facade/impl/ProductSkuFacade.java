package com.ruoyi.product.facade.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.SortBy;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.framework.mybatis.DynamicCondition;
import com.ruoyi.product.convert.ProductSkuCov;
import com.ruoyi.product.domain.ProductSku;
import com.ruoyi.product.facade.IProductSkuFacade;
import com.ruoyi.product.service.ProductSkuService;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.product.model.query.ProductSkuQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 商品基础数据Service接口
 *
 * @author ruoyi
 * @date 2025-09-21
 */
@Slf4j
@Service
public class ProductSkuFacade implements IProductSkuFacade {

    @Autowired
    private ProductSkuService productSkuService;

    @Override
    public List<ProductSkuBO> list(ProductSkuQuery query, SortBy sort) {
        return ProductSkuCov.INSTANCE.listToBO(productSkuService.list(DynamicCondition.toWrapper(query,sort)));
    }

    @Override
    public PageBO<ProductSkuBO> pageList(ProductSkuQuery query, PageParamV2 pageParamV2) {
        PageUtils.startPage(pageParamV2);
        List<ProductSku> list = productSkuService.list(DynamicCondition.toWrapper(query, pageParamV2.getSort()));
        return PageUtils.fromList(list, ProductSkuCov.INSTANCE::listToBO);
    }

    @Override
    public ProductSkuBO getOne(ProductSkuQuery query) {
        return ProductSkuCov.INSTANCE.toBO(productSkuService.getOne(DynamicCondition.toWrapper(query)));
    }

    @Override
    public boolean update(ProductSkuParam param, ProductSkuQuery query) {
        ProductSku queryDomain = ProductSkuCov.INSTANCE.queryToDomain(query);
        return productSkuService.update(ProductSkuCov.INSTANCE.paramToDomain(param), new QueryWrapper<>(queryDomain));
    }

    @Override
    public ProductSkuBO save(ProductSkuParam param) {
        ProductSku productSku = ProductSkuCov.INSTANCE.paramToDomain(param);
        productSku.setCreateTime(DateUtil.date());
        boolean save = productSkuService.save(productSku);

        return ProductSkuCov.INSTANCE.toBO(productSku);
    }

    @Override
    public long count(ProductSkuQuery productSkuQuery) {
         return productSkuService.count(DynamicCondition.toWrapper(productSkuQuery));
    }


}
