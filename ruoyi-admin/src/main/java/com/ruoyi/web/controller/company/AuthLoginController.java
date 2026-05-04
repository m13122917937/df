package com.ruoyi.web.controller.company;

import com.ruoyi.biz.company.AuthLoginBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.web.form.company.AuthLoginInfoForm;
import com.ruoyi.web.form.company.AuthLoginSaveForm;
import com.ruoyi.web.form.company.AuthLoginUpForm;
import com.ruoyi.web.vo.company.AuthLoginInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 授权登录Controller
 *
 * @author ruoyi
 * @date 2026-04-18
 */
@Slf4j
@RestController
@RequestMapping("/company/auth")
public class AuthLoginController extends BaseController {

    @Autowired
    AuthLoginBizService authLoginBizService;

    /**
     * 授权登录
     *
     * @param form 登录表单
     * @return 登录结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody AuthLoginInfoForm form) {
        log.info("请求参数：{}", JacksonUtil.toJson(form));
        AuthLoginInfoVO vo = authLoginBizService.login(form);
        return AjaxResult.success(vo);
    }

    /**
     * 注册授权账户
     *
     * @param form 注册表单
     * @return 注册结果
     */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody AuthLoginSaveForm form) {
        log.info("请求参数：{}", JacksonUtil.toJson(form));
        AuthLoginInfoVO vo = authLoginBizService.reg(form);
        return AjaxResult.success(vo);
    }

    /**
     * 更新授权信息
     *
     * @param form 更新表单
     * @return 更新结果
     */
    @PutMapping("/update")
    public AjaxResult update(@RequestBody AuthLoginUpForm form) {
        AuthLoginInfoVO vo = authLoginBizService.up(form);
        return AjaxResult.success(vo);
    }

}
