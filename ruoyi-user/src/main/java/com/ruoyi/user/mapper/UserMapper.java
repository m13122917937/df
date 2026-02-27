package com.ruoyi.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.user.domain.User;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.model.query.UserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{

    List<CompanyUserDTO> companyUser(UserQuery userQuery);


}
