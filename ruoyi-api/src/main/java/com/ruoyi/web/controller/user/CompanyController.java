package com.ruoyi.web.controller.user;

import com.ruoyi.biz.company.CompanyBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.vo.user.CompanyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("company")
public class CompanyController extends BaseController {

    @Autowired
    CompanyBizService companyBizService;

    @GetMapping("/auth")
    public AjaxResult auth() {

        String authUrl = companyBizService.getOrgAuthUrl(getDeptId(), getUserId());
        return AjaxResult.success().put(AjaxResult.DATA_TAG, authUrl);
    }

    @GetMapping("info")
    public AjaxResult info() {

        CompanyVO companyVO = companyBizService.info(getDeptId());

        return AjaxResult.success(companyVO);
    }

    @PostMapping("/auth/{companyId}/{userId}")
    public AjaxResult callBack(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId, @RequestBody() String json) {

        log.info("请求回调：{}，{}，{}", companyId, userId, json);
        companyBizService.authCallBack(companyId, userId);
        return AjaxResult.success();
    }

}
