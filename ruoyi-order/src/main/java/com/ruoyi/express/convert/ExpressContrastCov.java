package com.ruoyi.express.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.express.domain.ExpressContrast;
import com.ruoyi.express.model.bo.ExpressContrastBO;
import com.ruoyi.express.model.query.ExpressContrastQuery;
import com.ruoyi.express.model.param.ExpressContrastParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ExpressContrastCov {

    ExpressContrastCov INSTANCE = Mappers.getMapper(ExpressContrastCov.class);


    List<ExpressContrastBO>  listToBO(List<ExpressContrast>  list );

    ExpressContrastBO   toBO(ExpressContrast  list );

    ExpressContrast  queryToDomain(ExpressContrastQuery query);

    ExpressContrast  paramToDomain(ExpressContrastParam param);
}

