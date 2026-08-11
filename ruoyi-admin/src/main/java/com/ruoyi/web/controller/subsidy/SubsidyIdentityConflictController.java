package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyIdentityConflictBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 国补微信身份冲突审阅接口。 */
@RestController
@RequestMapping("/subsidy/wechat-identity-conflicts")
public class SubsidyIdentityConflictController {
    private final SubsidyIdentityConflictBizService conflictBizService;
    public SubsidyIdentityConflictController(final SubsidyIdentityConflictBizService conflictBizService) { this.conflictBizService = conflictBizService; }
    /** 查询冲突列表，仅供人工审阅。 */
    @GetMapping public AjaxResult list(@RequestParam(required = false) final String status) { return AjaxResult.success(conflictBizService.list(status)); }
}
