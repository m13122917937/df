package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbMemberAddress;
import com.ruoyi.subsidy.mapper.GbMemberAddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/** 国补会员地址领域服务。 */
@Service
public class GbMemberAddressService extends ServiceImpl<GbMemberAddressMapper, GbMemberAddress> {

    /** 查询会员地址列表。 */
    public List<GbMemberAddress> listByMemberId(final Long memberId) {
        return baseMapper.selectByMemberId(memberId);
    }

    /** 查询归属会员的地址。 */
    public GbMemberAddress getByIdAndMemberId(final Long id, final Long memberId) {
        return baseMapper.selectByIdAndMemberId(id, memberId);
    }

    /** 清空会员默认地址标记。 */
    public void clearDefaultByMemberId(final Long memberId) {
        baseMapper.clearDefaultByMemberId(memberId);
    }
}
