package com.ruoyi.biz.miniapp;

import com.ruoyi.subsidy.domain.GbMemberAddress;
import com.ruoyi.subsidy.facade.IGbMemberAddressFacade;
import com.ruoyi.subsidy.model.param.GbMemberAddressParam;
import com.ruoyi.web.convert.miniapp.MiniappAddressWebConvert;
import com.ruoyi.web.form.miniapp.MiniappAddressRequest;
import com.ruoyi.web.vo.miniapp.MiniappAddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 小程序地址应用编排。 */
@Component
@RequiredArgsConstructor
public class MiniappAddressBizService {
    private final IGbMemberAddressFacade addressFacade;

    /** 查询当前会员地址。 */
    public List<MiniappAddressVO> list(final Long memberId) {
        return MiniappAddressWebConvert.INSTANCE.toVOList(addressFacade.list(memberId));
    }

    /** 新增或修改当前会员地址。 */
    public MiniappAddressVO save(final Long memberId, final MiniappAddressRequest request) {
        GbMemberAddressParam param = MiniappAddressWebConvert.INSTANCE.toParam(request);
        param.setMemberId(memberId);
        GbMemberAddress address = addressFacade.save(param);
        return MiniappAddressWebConvert.INSTANCE.toVO(address);
    }

    /** 删除当前会员地址。 */
    public void remove(final Long memberId, final Long addressId) {
        addressFacade.remove(memberId, addressId);
    }
}
