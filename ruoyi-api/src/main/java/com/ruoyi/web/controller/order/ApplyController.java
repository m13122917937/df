package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.ApplyBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.web.form.order.CancelApplyForm;
import com.ruoyi.web.vo.order.ApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "订单申请接口")
@RestController
@RequestMapping("apply")
public class ApplyController extends BaseController {

    @Autowired
    ApplyBizService applyBizService;

    @ApiOperation("毁单申请")
    @PostMapping("cancel")
    public AjaxResult apply(@Validated @RequestBody CancelApplyForm cancelApplyForm) {

        LoginUser loginUser = getLoginUser();

        applyBizService.applyCancel(cancelApplyForm, loginUser.getDeptId(), loginUser.getUserId());

        return AjaxResult.success();
    }

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

}
