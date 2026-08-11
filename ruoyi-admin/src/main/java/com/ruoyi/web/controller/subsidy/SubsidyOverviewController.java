package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyOverviewBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 国补商城后台概览接口。 */
@RestController
@RequestMapping("/subsidy")
public class SubsidyOverviewController {
    private final SubsidyOverviewBizService overviewBizService;

    public SubsidyOverviewController(final SubsidyOverviewBizService overviewBizService) {
        this.overviewBizService = overviewBizService;
    }

    /** 查询国补商城概览。 */
    @GetMapping("/overview")
    public AjaxResult overview() {
        return AjaxResult.success(overviewBizService.getOverview());
    }
}
