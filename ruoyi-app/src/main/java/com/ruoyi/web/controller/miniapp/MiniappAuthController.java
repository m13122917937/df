package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.miniapp.MiniappAuthBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.miniapp.MiniappLoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序认证接口。
 */
@RestController
@RequestMapping("/miniapp/auth")
public class MiniappAuthController {
    private final MiniappAuthBizService authBizService;

    public MiniappAuthController(final MiniappAuthBizService authBizService) {
        this.authBizService = authBizService;
    }

    /**
     * 静默登录。
     *
     * @param request 登录请求
     * @return 登录结果
     */
    @Anonymous
    @PostMapping("/silent-login")
    public AjaxResult silentLogin(@RequestBody final MiniappLoginRequest request) {
        Assert.notBlank(request.getCode(), "微信登录凭证不能为空");
        return AjaxResult.success(authBizService.silentLogin(request.getCode()));
    }
}
