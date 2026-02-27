package com.ruoyi.mapper.user;

import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.web.form.user.CompanyCapitalLogForm;
import com.ruoyi.web.vo.user.CompanyCapitalLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CapitalConvert {

    CapitalConvert INSTANCE = Mappers.getMapper(CapitalConvert.class);


    CompanyCapitalLogQuery toLogQuery(CompanyCapitalLogForm logParam);

    @Mapping(source = "type", target = "typeName", qualifiedByName = "typeNameToString")
    List<CompanyCapitalLogVO> toVO(List<CompanyCapitalLogBO> data);

    @Named("typeNameToString")
    default String typeNameToString(Integer type) {
        if (type == null) return null;
        return CompanyCapitalConsts.LogTypes.fromValue(type).getMsg();
    }


}
