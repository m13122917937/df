package com.ruoyi.bill.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.Deduction;
import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.bill.model.query.DeductionQuery;
import com.ruoyi.bill.model.param.DeductionParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeductionCov {

    DeductionCov INSTANCE = Mappers.getMapper(DeductionCov.class);


    List<DeductionBO>  listToBO(List<Deduction>  list );

    DeductionBO   toBO(Deduction  list );

    Deduction  queryToDomain(DeductionQuery query);

    Deduction  paramToDomain(DeductionParam param);

    Deduction toEntity(DeductionParam param);

}

