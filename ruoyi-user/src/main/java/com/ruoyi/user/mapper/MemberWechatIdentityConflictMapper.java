package com.ruoyi.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.user.domain.MemberWechatIdentityConflict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员微信身份冲突 Mapper。
 */
@Mapper
public interface MemberWechatIdentityConflictMapper extends BaseMapper<MemberWechatIdentityConflict> {
    /** 查询未处理的同源身份冲突。 */
    MemberWechatIdentityConflict selectOpenConflict(@Param("unionId") String unionId,
                                                    @Param("sourceMemberId") Long sourceMemberId,
                                                    @Param("targetMemberId") Long targetMemberId);
    /** 查询身份冲突列表。 */
    List<MemberWechatIdentityConflict> selectListByStatus(@Param("status") String status);
}
