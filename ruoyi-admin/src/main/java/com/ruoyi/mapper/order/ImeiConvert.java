package com.ruoyi.mapper.order;

import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.bo.ImeiSkuRelBO;
import com.ruoyi.order.model.query.ImeiSkuRelQuery;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.vo.order.ImeiRelVO;
import com.ruoyi.web.vo.order.ImeiVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImeiConvert {

    ImeiConvert INSTANCE = Mappers.getMapper(ImeiConvert.class);


    List<ImeiVO> toVo(List<ImeiBO> list);

    List<ImeiRelVO> toRelVo(List<ImeiSkuRelBO> data);


    ImeiSkuRelQuery toQuery(ImeiForm imeiForm);


}
