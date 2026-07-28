package com.ruoyi.web.convert.master;

import com.ruoyi.product.model.bo.ProductSkuBO;
import com.ruoyi.product.model.query.ProductSkuQuery;
import com.ruoyi.web.vo.master.MasterProductQueryRequest;
import com.ruoyi.web.vo.master.MasterProductVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商品主数据 Web 对象转换器。
 */
@Mapper
public interface MasterProductWebConvert {

    /** 转换器实例。 */
    MasterProductWebConvert INSTANCE = Mappers.getMapper(MasterProductWebConvert.class);

    /**
     * 转换商品查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    ProductSkuQuery toQuery(MasterProductQueryRequest source);

    /**
     * 批量转换商品列表响应。
     *
     * @param source 商品业务对象集合
     * @return Web 响应集合
     */
    List<MasterProductVO> toVOList(List<ProductSkuBO> source);
}
