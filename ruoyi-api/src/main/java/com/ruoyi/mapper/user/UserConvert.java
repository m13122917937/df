package com.ruoyi.mapper.user;

import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.web.vo.user.CompanyVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);


    List<Object> toVoList(List<UserBO> data);

}
