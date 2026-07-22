package com.ruoyi.user.facade.impl;

import com.ruoyi.user.facade.IWeComLoginFacade;
import com.ruoyi.user.service.WeComLoginService;
import com.ruoyi.user.model.bo.WeComPrivateAuthorizationBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

/** 企业微信登录领域接口实现。 */
@Component
@RequiredArgsConstructor
public class WeComLoginFacade implements IWeComLoginFacade {
    private final WeComLoginService weComLoginService;

    /** {@inheritDoc} */
    @Override
    public String createAuthorizationUrl() {
        return weComLoginService.createAuthorizationUrl();
    }

    /** {@inheritDoc} */
    @Override
    public String createLoginTicket(String code, String state) {
        return weComLoginService.createLoginTicket(code, state);
    }

    /** {@inheritDoc} */
    @Override
    public String exchangeTicket(String ticket) {
        return weComLoginService.exchangeTicket(ticket);
    }

    /** {@inheritDoc} */
    @Override
    public String buildLoginPageUrl(String message) {
        return weComLoginService.buildLoginPageUrl(message);
    }

    @Override
    public void writePrivateAuthorizationQr(String sessionId, OutputStream outputStream) throws IOException {
        weComLoginService.writePrivateAuthorizationQr(sessionId, outputStream);
    }

    @Override
    public void completePrivateAuthorization(String code, String sessionId) {
        weComLoginService.completePrivateAuthorization(code, sessionId);
    }

    @Override
    public WeComPrivateAuthorizationBO getPrivateAuthorizationStatus(String sessionId) {
        return weComLoginService.getPrivateAuthorizationStatus(sessionId);
    }

    @Override
    public boolean isPrivateAuthorizationState(String state) {
        return weComLoginService.isPrivateAuthorizationState(state);
    }
}
