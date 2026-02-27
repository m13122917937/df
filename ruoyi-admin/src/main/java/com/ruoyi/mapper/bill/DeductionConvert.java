package com.ruoyi.mapper.bill;

import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.bill.model.param.DeductionParam;
import com.ruoyi.bill.model.query.DeductionQuery;
import com.ruoyi.web.form.bill.DeductionListForm;
import com.ruoyi.web.form.bill.DeductionSaveForm;
import com.ruoyi.web.vo.bill.DeductionVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeductionConvert {

    DeductionConvert INSTANCE = Mappers.getMapper(DeductionConvert.class);

    DeductionQuery toQueryParam(DeductionListForm deductionListForm);

    List<DeductionVO> toVOList(List<DeductionBO> data);

    DeductionParam toParam(DeductionSaveForm deductionSaveForm);

}
