package com.ruoyi.mapper.product;

import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.param.ProductSkuParam;
import com.ruoyi.web.form.product.ProductSkuForm;
import com.ruoyi.web.vo.product.ProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductConvert {

    ProductConvert INSTANCE = Mappers.getMapper(ProductConvert.class);


    ProductVO toVO(ProductSkuBO one);

    List<ProductVO> toVOList(List<ProductSkuBO> productList);

    ProductSkuParam toParam(ProductSkuForm form);

}
