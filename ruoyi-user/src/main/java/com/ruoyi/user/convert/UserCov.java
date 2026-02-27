package com.ruoyi.user.convert;

import com.ruoyi.user.domain.User;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.param.UserParam;
import com.ruoyi.user.model.query.UserQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserCov {

    UserCov INSTANCE = Mappers.getMapper(UserCov.class);


    List<UserBO> domainToBoList(List<User> list);

    User queryToBo(UserQuery userQuery);

    UserBO domainToBo(User user);

    User paramToBo(UserParam userParam);

    List<UserBO> dtoToBO(List<CompanyUserDTO> companyUserDTOS);


}
