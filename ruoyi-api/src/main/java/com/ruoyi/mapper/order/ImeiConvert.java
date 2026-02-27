package com.ruoyi.mapper.order;

import com.ruoyi.order.model.bo.ImeiBO;
import com.ruoyi.order.model.param.ImeiParam;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.web.vo.order.ImeiVO;
import com.ruoyi.web.vo.user.CompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ImeiConvert {

    ImeiConvert INSTANCE = Mappers.getMapper(ImeiConvert.class);


    ImeiParam boToParam(ImeiBO imeiBO);

    List<ImeiVO> listvo(List<ImeiBO> list);

    List<ImeiVO> toVo(List<ImeiBO> list);

}
