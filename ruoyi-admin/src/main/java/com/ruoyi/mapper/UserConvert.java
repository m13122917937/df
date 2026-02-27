package com.ruoyi.mapper;

import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.web.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {


    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);


    List<UserVO> toUserVoList(List<UserBO> userBOS);

    
}
