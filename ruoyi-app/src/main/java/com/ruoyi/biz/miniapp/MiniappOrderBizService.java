package com.ruoyi.biz.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.config.properties.WxMiniappProperties;
import com.ruoyi.subsidy.facade.IGbOrderFacade;
import com.ruoyi.subsidy.model.bo.GbOrderBO;
import com.ruoyi.subsidy.model.param.GbOrderCreateParam;
import com.ruoyi.web.form.miniapp.MiniappOrderCreateRequest;
import com.ruoyi.user.facade.IWechatIdentityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 小程序订单应用编排。
 */
@Component
@RequiredArgsConstructor
public class MiniappOrderBizService {
    private final IGbOrderFacade orderFacade;
    private final IWechatIdentityFacade identityFacade;
    private final WxMiniappProperties miniappProperties;

    /**
     * 创建订单。
     *
     * @param memberId 当前会员ID
     * @param request 下单请求
     * @return 订单信息
     */
    public GbOrderBO create(final Long memberId, final MiniappOrderCreateRequest request) {
        Assert.isTrue(identityFacade.isPurchaseAllowed(memberId, "MINIAPP", miniappProperties.getAppId()),
                "微信身份尚未完成归并，暂不支持下单");
        GbOrderCreateParam param = new GbOrderCreateParam().setMemberId(memberId).setSkuId(request.getSkuId())
                .setQuantity(request.getQuantity()).setReceiverName(request.getReceiverName())
                .setReceiverPhone(request.getReceiverPhone()).setProvinceName(request.getProvinceName())
                .setCityName(request.getCityName()).setDistrictName(request.getDistrictName())
                .setDetailAddress(request.getDetailAddress());
        return orderFacade.create(param);
    }

    /** 查询当前会员订单列表。 */
    public List<GbOrderBO> list(final Long memberId) {
        return orderFacade.listByMemberId(memberId);
    }

    /** 查询当前会员所属订单详情。 */
    public GbOrderBO get(final Long memberId, final String orderNo) {
        return orderFacade.getByMemberAndOrderNo(memberId, orderNo);
    }

    /** 确认当前会员订单收货。 */
    public void confirmReceived(final Long memberId, final String orderNo) {
        orderFacade.confirmReceived(memberId, orderNo);
    }

    /** 取消当前会员待支付订单。 */
    public void cancel(final Long memberId, final String orderNo) {
        orderFacade.cancel(memberId, orderNo);
    }
}
