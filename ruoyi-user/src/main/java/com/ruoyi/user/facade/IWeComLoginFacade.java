package com.ruoyi.user.facade;

/** 企业微信登录领域接口。 */
public interface IWeComLoginFacade {
    /**
     * 创建企业微信扫码授权地址。
     *
     * @return 授权地址
     */
    String createAuthorizationUrl();

    /**
     * 创建一次性登录票据。
     *
     * @param code 企业微信授权码
     * @param state 授权状态码
     * @return 登录票据
     */
    String createLoginTicket(String code, String state);

    /**
     * 兑换一次性登录票据。
     *
     * @param ticket 登录票据
     * @return JWT
     */
    String exchangeTicket(String ticket);

    /**
     * 构建前端登录页地址。
     *
     * @param message 登录提示
     * @return 前端登录页地址
     */
    String buildLoginPageUrl(String message);
}
