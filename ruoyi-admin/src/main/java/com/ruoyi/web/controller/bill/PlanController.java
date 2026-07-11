package com.ruoyi.web.controller.bill;

import com.ruoyi.biz.bill.PlanBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.bill.PlanForm;
import com.ruoyi.web.vo.bill.BillPayPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/bill/plan")
public class PlanController extends BaseController {

    @Autowired
    PlanBizService planBizService;

    @GetMapping("/{planId}")
    public AjaxResult info(@PathVariable("planId") Long planId) {

        BillPayPlanVO plan = planBizService.plan(planId);

        return AjaxResult.success(plan);
    }




    @PostMapping("/update")
    public AjaxResult info(@Validated @RequestBody PlanForm planForm) {

        planBizService.update(planForm, getUserId());

        return AjaxResult.success();
    }


}
