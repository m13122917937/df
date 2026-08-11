package com.ruoyi.user.facade;

import com.ruoyi.user.model.bo.WechatIdentityBO;
import com.ruoyi.user.model.param.WechatIdentityParam;

/**
 * 微信统一身份领域出口。
 */
public interface IWechatIdentityFacade {

    /**
     * 解析或创建微信身份。
     *
     * @param param 微信身份参数
     * @return 解析结果
     */
    WechatIdentityBO resolve(WechatIdentityParam param);

    /**
     * 查询会员指定渠道的 OpenID。
     *
     * @param memberId 会员ID
     * @param channel 微信渠道
     * @param appId 微信AppID
     * @return OpenID，不存在时为空
     */
    String getOpenId(Long memberId, String channel, String appId);

    /**
     * 判断会员是否已完成可购买的微信身份归并。
     *
     * @param memberId 会员 ID
     * @param channel 微信渠道
     * @param appId 微信 AppID
     * @return 是否允许下单和支付
     */
    boolean isPurchaseAllowed(Long memberId, String channel, String appId);

    /** 查询待人工审阅的身份冲突。 */
    java.util.List<com.ruoyi.user.domain.MemberWechatIdentityConflict> listConflicts(String status);
}
