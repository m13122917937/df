package com.ruoyi.subsidy.convert;

import com.ruoyi.subsidy.domain.GbCategory;
import com.ruoyi.subsidy.domain.GbProduct;
import com.ruoyi.subsidy.domain.GbProductSku;
import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.subsidy.model.param.GbCategoryParam;
import com.ruoyi.subsidy.model.param.GbProductParam;
import com.ruoyi.subsidy.model.param.GbProductSkuParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 国补商城目录对象转换器。
 */
@Mapper
public interface GbCatalogConvert {
    GbCatalogConvert INSTANCE = Mappers.getMapper(GbCatalogConvert.class);

    GbCategoryBO toCategoryBO(GbCategory source);
    List<GbCategoryBO> toCategoryBOList(List<GbCategory> source);
    GbCategory toCategoryEntity(GbCategoryParam source);
    GbProductBO toProductBO(GbProduct source);
    List<GbProductBO> toProductBOList(List<GbProduct> source);
    GbProduct toProductEntity(GbProductParam source);
    GbProductSkuBO toSkuBO(GbProductSku source);
    List<GbProductSkuBO> toSkuBOList(List<GbProductSku> source);
    GbProductSku toSkuEntity(GbProductSkuParam source);
}
