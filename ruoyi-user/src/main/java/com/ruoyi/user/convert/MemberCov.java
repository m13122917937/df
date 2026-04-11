package com.ruoyi.user.convert;

import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.user.model.param.MemberParam;
import com.ruoyi.user.model.query.MemberQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MemberCov {

    MemberCov INSTANCE = Mappers.getMapper(MemberCov.class);


    List<MemberBO> domainToBoList(List<Member> list);

    Member queryToBo(MemberQuery memberQuery);

    MemberBO domainToBo(Member member);

    Member paramToBo(MemberParam memberParam);

    List<MemberBO> dtoToBO(List<CompanyUserDTO> companyUserDTOS);


}
