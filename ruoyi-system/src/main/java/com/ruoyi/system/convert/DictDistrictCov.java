package com.ruoyi.system.convert;

import com.ruoyi.system.domain.DictDistrict;
import com.ruoyi.system.model.bo.DictDistrictBO;
import com.ruoyi.system.model.query.DictDistrictQuery;
import com.ruoyi.system.model.param.DictDistrictParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictDistrictCov {

    DictDistrictCov INSTANCE = Mappers.getMapper(DictDistrictCov.class);


    List<DictDistrictBO> listToBO(List<DictDistrict> list);

    DictDistrictBO toBO(DictDistrict list);

    DictDistrict queryToDomain(DictDistrictQuery query);

    DictDistrict paramToDomain(DictDistrictParam param);
}

