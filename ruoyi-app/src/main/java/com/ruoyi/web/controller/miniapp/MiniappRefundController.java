package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.miniapp.MiniappRefundBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.form.miniapp.MiniappRefundApplyRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 小程序退款接口。 */
@RestController
@RequestMapping("/miniapp/orders")
public class MiniappRefundController {
    private final MiniappRefundBizService refundBizService;

    public MiniappRefundController(final MiniappRefundBizService refundBizService) {
        this.refundBizService = refundBizService;
    }

    /** 提交未发货订单的整单退款申请。 */
    @PostMapping("/{orderNo}/refunds")
    public AjaxResult apply(@PathVariable final String orderNo, @RequestBody final MiniappRefundApplyRequest request) {
        Long memberId = SecurityUtils.getUserId();
        Assert.notNull(memberId, "登录已失效");
        return AjaxResult.success(refundBizService.apply(memberId, orderNo, request));
    }
}
