package com.ruoyi.biz.system;

import com.ruoyi.user.facade.IWeComLoginFacade;
import com.ruoyi.user.model.bo.WeComPrivateAuthorizationBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

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

    public void writePrivateAuthorizationQr(String sessionId, OutputStream outputStream) throws IOException {
        weComLoginFacade.writePrivateAuthorizationQr(sessionId, outputStream);
    }

    public void completePrivateAuthorization(String code, String sessionId) {
        weComLoginFacade.completePrivateAuthorization(code, sessionId);
    }

    public WeComPrivateAuthorizationBO getPrivateAuthorizationStatus(String sessionId) {
        return weComLoginFacade.getPrivateAuthorizationStatus(sessionId);
    }

    public boolean isPrivateAuthorizationState(String state) {
        return weComLoginFacade.isPrivateAuthorizationState(state);
    }
}
