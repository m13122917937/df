package com.ruoyi.mapper.salesReturn;

import com.ruoyi.order.model.bo.OrderBO;
import com.ruoyi.order.model.bo.SalesReturnBO;
import com.ruoyi.order.model.param.SalesReturnParam;
import com.ruoyi.order.model.query.SalesReturnQuery;
import com.ruoyi.web.form.order.SalesReturnListForm;
import com.ruoyi.web.form.order.SalesReturnSaveForm;
import com.ruoyi.web.vo.order.SalesReturnOrderDetailVO;
import com.ruoyi.web.vo.order.SalesReturnVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SalesReturnConvert {

    SalesReturnConvert INSTANCE = Mappers.getMapper(SalesReturnConvert.class);

    SalesReturnQuery toQuery(SalesReturnListForm form);

    SalesReturnParam toParam(SalesReturnSaveForm form);

    SalesReturnVO toVO(SalesReturnBO bo);

    List<SalesReturnVO> toVOList(List<SalesReturnBO> data);

    SalesReturnOrderDetailVO toOrderDetailVO(OrderBO orderBO);
}
