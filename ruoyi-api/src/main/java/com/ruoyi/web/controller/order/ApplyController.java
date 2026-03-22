package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.ApplyBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.web.form.order.CancelApplyForm;
import com.ruoyi.web.vo.order.ApplyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("apply")
public class ApplyController extends BaseController {

    @Autowired
    ApplyBizService applyBizService;


    @PostMapping("cancel")
    public AjaxResult apply(@Validated @RequestBody CancelApplyForm cancelApplyForm) {

        LoginUser loginUser = getLoginUser();

        applyBizService.applyCancel(cancelApplyForm, loginUser.getDeptId(), loginUser.getUserId());

        return AjaxResult.success();
    }




    @GetMapping("{orderCode}")
    public AjaxResult queryAppy(@PathVariable("orderCode") String orderCode) {
        Assert.notBlank(orderCode, "订单号不存在");
        ApplyVO applyVO = applyBizService.queryApply(orderCode);
        return AjaxResult.success(applyVO);

    }

}
