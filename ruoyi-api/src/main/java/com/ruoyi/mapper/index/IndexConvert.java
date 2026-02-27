package com.ruoyi.mapper.index;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.order.model.bo.HangingOrderBO;
import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.ProductBO;
import com.ruoyi.order.model.bo.SkuBO;
import com.ruoyi.order.model.param.HangingOrderParam;
import com.ruoyi.order.model.query.OrderQuery;
import com.ruoyi.order.model.query.OrderTabCountQuery;
import com.ruoyi.web.form.index.ProductForm;
import com.ruoyi.web.vo.index.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IndexConvert {

    IndexConvert INSTANCE = Mappers.getMapper(IndexConvert.class);


    List<IndexTabVO> toIndexTab(List<SysDictData> indexBrandTab);

    List<IndexProductVO> toIndexProductVO(List<ProductBO> productBOS);


    OrderTabCountQuery toOrderTabCountQuery(ProductForm productForm);

    List<IndexSkuVO> toIndexSkuVO(List<SkuBO> productBOS);


    OrderQuery toOrderQuery(ProductForm productForm);

    List<IndexSkuListVO> toIndexSkuListVOList(List<OrderBO> data);

    @Mapping(target = "otherRequire", ignore = true)
    IndexOrderInfoVO toIndexSkuInfoVO(OrderBO orderBO, HangingOrderBO hangingOrderBO);

    HangingOrderParam tohangingOrderParam(HangingOrderBO hangingOrderBO);


}
