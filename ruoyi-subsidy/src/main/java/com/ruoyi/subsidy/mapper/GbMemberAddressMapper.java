package com.ruoyi.subsidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.subsidy.domain.GbMemberAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 国补会员地址 Mapper。
 */
@Mapper
public interface GbMemberAddressMapper extends BaseMapper<GbMemberAddress> {

    /** 查询会员地址列表。 */
    List<GbMemberAddress> selectByMemberId(@Param("memberId") Long memberId);

    /** 查询归属会员的地址。 */
    GbMemberAddress selectByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

    /** 清空会员默认地址标记。 */
    int clearDefaultByMemberId(@Param("memberId") Long memberId);
}
