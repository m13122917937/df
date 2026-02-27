package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.ApplyBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.ApplyfailForm;
import com.ruoyi.web.vo.apply.ApplyVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "订单申请接口")
@RestController
@RequestMapping("/order/apply")
public class OrderApplyController {

    @Autowired
    ApplyBizService applyBizService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ApplyVO.class)
    })
    @ApiOperation("查询毁单明细")
    @GetMapping("{orderCode}")
    public AjaxResult queryAppy(@PathVariable("orderCode") String orderCode) {
        Assert.notBlank(orderCode, "订单号不存在");
        ApplyVO applyVO = applyBizService.queryApply(orderCode);
        return AjaxResult.success(applyVO);
    }

    @ApiOperation("同意订单申请")
    @GetMapping("/agreement/{applyId}")
    public AjaxResult agreement(@PathVariable("applyId") Long applyId) {
        applyBizService.agreement(applyId);
        return AjaxResult.success();
    }


    @ApiOperation("拒绝订单申请")
    @PostMapping("/fail")
    public AjaxResult fail(@RequestBody ApplyfailForm applyfailForm) {
        applyBizService.fail(applyfailForm.getApplyId(), applyfailForm.getRefuseRemark());
        return AjaxResult.success();
    }

}
