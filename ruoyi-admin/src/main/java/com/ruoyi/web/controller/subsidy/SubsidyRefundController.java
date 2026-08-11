package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyRefundBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 国补后台退款审核接口。 */
@RestController
@RequestMapping("/subsidy/refunds")
public class SubsidyRefundController {
    private final SubsidyRefundBizService refundBizService;

    public SubsidyRefundController(final SubsidyRefundBizService refundBizService) {
        this.refundBizService = refundBizService;
    }

    /** 审核通过并发起原路退款。 */
    @PostMapping("/{refundNo}/approve")
    public AjaxResult approve(@PathVariable final String refundNo) {
        refundBizService.approve(refundNo);
        return AjaxResult.success();
    }

    /** 查询退款列表。 */
    @GetMapping
    public AjaxResult list(@RequestParam(required = false) final String refundStatus) {
        return AjaxResult.success(refundBizService.list(refundStatus));
    }
}
