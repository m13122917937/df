package com.ruoyi.product.convert;

import com.ruoyi.product.domain.ProductSku;
import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.product.model.param.ProductSkuParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductSkuCov {

    ProductSkuCov INSTANCE = Mappers.getMapper(ProductSkuCov.class);


    List<ProductSkuBO>   listToBO(List<ProductSku>  list );

    ProductSkuBO   toBO(ProductSku  list );

    ProductSku  queryToDomain(ProductSkuQuery query);

    ProductSku  paramToDomain(ProductSkuParam param);
}

