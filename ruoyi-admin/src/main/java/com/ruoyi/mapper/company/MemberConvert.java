package com.ruoyi.mapper.company;

import com.ruoyi.user.model.bo.MemberBO;
import com.ruoyi.web.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MemberConvert {

    MemberConvert INSTANCE = Mappers.getMapper(MemberConvert.class);

    List<UserVO> toVoList(List<MemberBO> memberBOS);

}
