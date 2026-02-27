package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.WaitPushForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "待发布")
@RestController
@RequestMapping("/order/wait")
public class WaitController extends BaseController {

    @Autowired
    OrderBizService orderBizService;

    @ApiOperation("定向推单")
    @PostMapping("push")
    public AjaxResult push(@Validated @RequestBody WaitPushForm waitPushForm) {

        orderBizService.pushCompany(waitPushForm, getLoginUser());

        return AjaxResult.success();
    }

}
