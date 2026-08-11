package com.ruoyi.web.convert.miniapp;

import com.ruoyi.subsidy.model.bo.GbCategoryBO;
import com.ruoyi.subsidy.model.bo.GbProductBO;
import com.ruoyi.subsidy.model.bo.GbProductSkuBO;
import com.ruoyi.web.vo.miniapp.MiniappCategoryVO;
import com.ruoyi.web.vo.miniapp.MiniappProductVO;
import com.ruoyi.web.vo.miniapp.MiniappSkuVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 小程序目录 Web 转换器。
 */
@Mapper
public interface MiniappCatalogWebConvert {
    MiniappCatalogWebConvert INSTANCE = Mappers.getMapper(MiniappCatalogWebConvert.class);

    List<MiniappCategoryVO> toCategoryVOList(List<GbCategoryBO> source);
    MiniappProductVO toProductVO(GbProductBO source);
    List<MiniappProductVO> toProductVOList(List<GbProductBO> source);
    List<MiniappSkuVO> toSkuVOList(List<GbProductSkuBO> source);
}
