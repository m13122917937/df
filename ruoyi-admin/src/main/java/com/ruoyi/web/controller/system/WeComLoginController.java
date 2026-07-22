package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.biz.system.WeComLoginBizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** 企业微信扫码登录接口。 */
@RestController
@RequestMapping("/wecom/login")
@Slf4j
@RequiredArgsConstructor
public class WeComLoginController {
    private final WeComLoginBizService weComLoginBizService;

    /**
     * 获取企业微信扫码授权地址。
     *
     * @return 授权地址
     */
    @GetMapping("/qr")
    public AjaxResult qr() {
        AjaxResult result = AjaxResult.success();
        result.put("authorizationUrl", weComLoginBizService.createAuthorizationUrl());
        return result;
    }

    /**
     * 接收企业微信扫码回调。
     *
     * @param code 企业微信授权码
     * @param state 登录状态码
     * @param response HTTP 响应
     * @throws IOException 重定向失败
     */
    @GetMapping("/callback")
    public void callback(@RequestParam(required = false) String code, @RequestParam(required = false) String state,
                         HttpServletResponse response) throws IOException {
        if (weComLoginBizService.isPrivateAuthorizationState(state)) {
            weComLoginBizService.completePrivateAuthorization(code, state);
            String errorMessage = weComLoginBizService.getPrivateAuthorizationStatus(state).getErrorMessage();
            if (errorMessage == null) {
                writeAuthorizationCompleted(response);
            } else {
                writeAuthorizationFailed(response, errorMessage);
            }
            return;
        }
        try {
            String ticket = weComLoginBizService.createLoginTicket(code, state);
            if (ticket.startsWith("BIND:")) {
                redirectTop(response, weComLoginBizService.buildLoginPageUrl("") + "&wecomBindSession=" + ticket.substring(5));
                return;
            }
            redirectTop(response, weComLoginBizService.buildLoginPageUrl("") + "&wecomTicket=" + ticket);
        } catch (ServiceException exception) {
            log.warn("企业微信扫码登录回调失败：{}", exception.getMessage());
            redirectTop(response, weComLoginBizService.buildLoginPageUrl(exception.getMessage()));
        } catch (Exception exception) {
            log.error("企业微信扫码登录回调异常", exception);
            redirectTop(response, weComLoginBizService.buildLoginPageUrl("企业微信登录失败，请重新扫码"));
        }
    }

    /**
     * 兑换一次性登录票据。
     *
     * @param ticket 登录票据
     * @return JWT
     */
    @GetMapping("/exchange")
    public AjaxResult exchange(@RequestParam String ticket) {
        AjaxResult result = AjaxResult.success();
        result.put(Constants.TOKEN, weComLoginBizService.exchangeTicket(ticket));
        return result;
    }

    @GetMapping(value = "/private-qr", produces = "image/png")
    public void privateQr(@RequestParam String session, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");
        weComLoginBizService.writePrivateAuthorizationQr(session, response.getOutputStream());
    }

    @GetMapping("/private-status")
    public AjaxResult privateStatus(@RequestParam String session) {
        AjaxResult result = AjaxResult.success();
        try {
            result.put("ticket", weComLoginBizService.getPrivateAuthorizationStatus(session).getTicket());
            result.put("errorMessage", weComLoginBizService.getPrivateAuthorizationStatus(session).getErrorMessage());
        } catch (ServiceException exception) {
            log.warn("企业微信手机号授权状态查询失败：{}", exception.getMessage());
            result.put("errorMessage", exception.getMessage());
        }
        return result;
    }

    private void redirectTop(HttpServletResponse response, String target) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("<script>window.top.location.replace('" + target + "');</script>");
        writer.flush();
    }

    private void writeAuthorizationCompleted(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<html><body style='font-family:Arial;text-align:center;padding-top:80px'>手机号授权完成，请返回电脑端继续登录。</body></html>");
    }

    private void writeAuthorizationFailed(HttpServletResponse response, String errorMessage) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<html><body style='font-family:Arial;text-align:center;padding-top:80px'>手机号授权失败："
            + errorMessage + "。请返回电脑端重新扫码。</body></html>");
    }
}
