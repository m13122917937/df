package com.ruoyi.bill.convert;

import com.ruoyi.bill.domain.Contract;
import com.ruoyi.bill.model.bo.ContractBO;
import com.ruoyi.bill.model.param.ContractParam;
import com.ruoyi.bill.model.query.ContractQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContractCov {

    ContractCov INSTANCE = Mappers.getMapper(ContractCov.class);

    List<ContractBO> listToBO(List<Contract> list);

    ContractBO toBO(Contract entity);

    Contract queryToDomain(ContractQuery query);

    Contract paramToDomain(ContractParam param);
}
