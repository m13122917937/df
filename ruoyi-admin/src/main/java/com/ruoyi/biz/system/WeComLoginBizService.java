package com.ruoyi.biz.system;

import com.ruoyi.user.facade.IWeComLoginFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 企业微信登录业务编排。 */
@Component
@RequiredArgsConstructor
public class WeComLoginBizService {
    private final IWeComLoginFacade weComLoginFacade;

    /**
     * 获取企业微信扫码授权地址。
     *
     * @return 授权地址
     */
    public String createAuthorizationUrl() {
        return weComLoginFacade.createAuthorizationUrl();
    }

    /**
     * 创建登录票据。
     *
     * @param code 企业微信授权码
     * @param state 授权状态码
     * @return 登录票据
     */
    public String createLoginTicket(String code, String state) {
        return weComLoginFacade.createLoginTicket(code, state);
    }

    /**
     * 兑换登录票据。
     *
     * @param ticket 登录票据
     * @return JWT
     */
    public String exchangeTicket(String ticket) {
        return weComLoginFacade.exchangeTicket(ticket);
    }

    /**
     * 获取前端登录页地址。
     *
     * @param message 登录提示
     * @return 前端登录页地址
     */
    public String buildLoginPageUrl(String message) {
        return weComLoginFacade.buildLoginPageUrl(message);
    }
}
