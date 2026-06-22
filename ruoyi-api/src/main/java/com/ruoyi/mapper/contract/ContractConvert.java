package com.ruoyi.mapper.contract;

import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.query.ContractQuery;
import com.ruoyi.web.form.contract.ContractQueryForm;
import com.ruoyi.web.vo.contract.ContractVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContractConvert {

    ContractConvert INSTANCE = Mappers.getMapper(ContractConvert.class);

    ContractVO toVO(ContractBO bo);

    List<ContractVO> toVOList(List<ContractBO> list);

    @Mapping(source = "contractName", target = "contractNameLike")
    ContractQuery queryFormToQuery(ContractQueryForm form);
}
