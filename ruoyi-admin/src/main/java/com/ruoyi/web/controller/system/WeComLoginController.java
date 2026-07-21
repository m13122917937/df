package com.ruoyi.web.controller.system;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
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
        try {
            String ticket = weComLoginBizService.createLoginTicket(code, state);
            redirectTop(response, weComLoginBizService.buildLoginPageUrl("") + "&wecomTicket=" + ticket);
        } catch (Exception exception) {
            log.warn("企业微信扫码登录回调失败", exception);
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

    private void redirectTop(HttpServletResponse response, String target) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("<script>window.top.location.replace('" + target + "');</script>");
        writer.flush();
    }
}
