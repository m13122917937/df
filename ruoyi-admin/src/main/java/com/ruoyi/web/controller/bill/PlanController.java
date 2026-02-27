package com.ruoyi.web.controller.bill;

import com.ruoyi.biz.bill.PlanBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.bill.PlanForm;
import com.ruoyi.web.vo.bill.BillPayPlanVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "排款详情")
@RequestMapping("/bill/plan")
public class PlanController extends BaseController {

    @Autowired
    PlanBizService planBizService;
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = BillPayPlanVO.class)
    })
    @ApiOperation("查询今天待付款分页列表")
    @GetMapping("/{planId}")
    public AjaxResult info(@PathVariable("planId") Long planId) {

        BillPayPlanVO plan = planBizService.plan(planId);

        return AjaxResult.success(plan);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ApiOperation("上传付款凭证")
    @PostMapping("/update")
    public AjaxResult info(@Validated @RequestBody PlanForm planForm) {

        planBizService.update(planForm, getUserId());

        return AjaxResult.success();
    }


}
