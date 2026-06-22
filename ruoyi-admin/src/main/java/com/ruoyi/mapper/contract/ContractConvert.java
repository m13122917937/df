package com.ruoyi.mapper.contract;

import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.param.ContractParam;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.web.form.contract.ContractForm;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContractConvert {

    ContractConvert INSTANCE = Mappers.getMapper(ContractConvert.class);

    ContractParam formToParam(ContractForm form);

    ContractVO toVO(ContractBO bo);

    List<ContractVO> toVOList(List<ContractBO> list);

    @Mapping(source = "contractName", target = "contractNameLike")
    @Mapping(source = "createTimeStart", target = "gteCreateTime")
    @Mapping(source = "createTimeEnd", target = "lteCreateTime")
    @Mapping(source = "signedTimeStart", target = "gteSignedTime")
    @Mapping(source = "signedTimeEnd", target = "lteSignedTime")
    ContractQuery queryFormToQuery(ContractQueryForm form);
}
