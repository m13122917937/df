package com.ruoyi.subsidy.facade.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.subsidy.domain.GbMemberAddress;
import com.ruoyi.subsidy.facade.IGbMemberAddressFacade;
import com.ruoyi.subsidy.model.param.GbMemberAddressParam;
import com.ruoyi.subsidy.service.GbMemberAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补会员地址领域实现。 */
@Component
@RequiredArgsConstructor
public class GbMemberAddressFacade implements IGbMemberAddressFacade {
    private final GbMemberAddressService addressService;

    @Override
    public List<GbMemberAddress> list(final Long memberId) {
        return addressService.listByMemberId(memberId);
    }

    @Override
    public GbMemberAddress save(final GbMemberAddressParam param) {
        validate(param);
        GbMemberAddress address = param.getId() == null ? new GbMemberAddress().setMemberId(param.getMemberId())
                .setCreateTime(DateUtil.date()) : addressService.getByIdAndMemberId(param.getId(), param.getMemberId());
        Assert.notNull(address, "地址不存在");
        address.setReceiverName(param.getReceiverName()).setReceiverPhone(param.getReceiverPhone())
                .setProvinceName(param.getProvinceName()).setCityName(param.getCityName())
                .setDistrictName(param.getDistrictName()).setDetailAddress(param.getDetailAddress())
                .setDefaultAddress(Boolean.TRUE.equals(param.getDefaultAddress()) ? 1 : 0).setUpdateTime(DateUtil.date());
        if (Boolean.TRUE.equals(param.getDefaultAddress())) {
            addressService.clearDefaultByMemberId(param.getMemberId());
        }
        addressService.saveOrUpdate(address);
        return address;
    }

    @Override
    public void remove(final Long memberId, final Long addressId) {
        GbMemberAddress address = addressService.getByIdAndMemberId(addressId, memberId);
        Assert.notNull(address, "地址不存在");
        addressService.removeById(addressId);
    }

    private void validate(final GbMemberAddressParam param) {
        Assert.notNull(param.getMemberId(), "会员不能为空");
        Assert.notBlank(param.getReceiverName(), "收货人不能为空");
        Assert.notBlank(param.getReceiverPhone(), "收货电话不能为空");
        Assert.notBlank(param.getProvinceName(), "省份不能为空");
        Assert.notBlank(param.getCityName(), "城市不能为空");
        Assert.notBlank(param.getDistrictName(), "地区不能为空");
        Assert.notBlank(param.getDetailAddress(), "详细地址不能为空");
    }
}
