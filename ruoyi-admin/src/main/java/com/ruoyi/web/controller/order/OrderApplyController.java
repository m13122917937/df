package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.ApplyBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.ApplyfailForm;
import com.ruoyi.web.vo.apply.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order/apply")
public class OrderApplyController {

    @Autowired
    ApplyBizService applyBizService;




    @GetMapping("{orderCode}")
    public AjaxResult queryAppy(@PathVariable("orderCode") String orderCode) {
        Assert.notBlank(orderCode, "订单号不存在");
        ApplyVO applyVO = applyBizService.queryApply(orderCode);
        return AjaxResult.success(applyVO);
    }


    @GetMapping("/agreement/{applyId}")
    public AjaxResult agreement(@PathVariable("applyId") Long applyId) {
        applyBizService.agreement(applyId);
        return AjaxResult.success();
    }



    @PostMapping("/fail")
    public AjaxResult fail(@RequestBody ApplyfailForm applyfailForm) {
        applyBizService.fail(applyfailForm.getApplyId(), applyfailForm.getRefuseRemark());
        return AjaxResult.success();
    }

}
