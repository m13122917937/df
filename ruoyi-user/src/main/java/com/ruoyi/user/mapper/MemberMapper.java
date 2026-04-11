package com.ruoyi.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.user.domain.Member;
import com.ruoyi.user.domain.dto.CompanyUserDTO;
import com.ruoyi.user.model.query.MemberQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-06
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member>{

    List<CompanyUserDTO> companyUser(MemberQuery memberQuery);


}
