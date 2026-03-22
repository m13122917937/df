package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.AfterSalesBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.AftersalesForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
@RequestMapping("order/aftersales")
public class AfterSalesController extends BaseController {

    @Autowired
    AfterSalesBizService afterSalesBizService;


    @PostMapping("add")
    public AjaxResult add(@Validated @RequestBody AftersalesForm aftersalesForm) {

        afterSalesBizService.add(aftersalesForm, getLoginUser() );

        return AjaxResult.success();
    }


}
