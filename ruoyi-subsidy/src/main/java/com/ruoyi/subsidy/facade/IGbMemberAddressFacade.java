package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.domain.GbMemberAddress;
import com.ruoyi.subsidy.model.param.GbMemberAddressParam;

import java.util.List;

/** 国补会员地址领域出口。 */
public interface IGbMemberAddressFacade {
    /** 查询当前会员地址。 */
    List<GbMemberAddress> list(Long memberId);
    /** 新增或修改会员地址。 */
    GbMemberAddress save(GbMemberAddressParam param);
    /** 删除会员地址。 */
    void remove(Long memberId, Long addressId);
}
