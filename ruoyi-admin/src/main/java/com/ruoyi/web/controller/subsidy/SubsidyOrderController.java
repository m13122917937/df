package com.ruoyi.web.controller.subsidy;

import com.ruoyi.biz.subsidy.SubsidyOrderBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.subsidy.SubsidyShipmentForm;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 国补后台订单接口。 */
@RestController
@RequestMapping("/subsidy/orders")
public class SubsidyOrderController {
    private final SubsidyOrderBizService orderBizService;

    public SubsidyOrderController(final SubsidyOrderBizService orderBizService) {
        this.orderBizService = orderBizService;
    }

    /** 人工录入物流信息并发货。 */
    @PostMapping("/{orderNo}/shipment")
    public AjaxResult ship(@PathVariable final String orderNo, @RequestBody final SubsidyShipmentForm form) {
        orderBizService.ship(orderNo, form);
        return AjaxResult.success();
    }

    /** 查询后台订单列表。 */
    @GetMapping
    public AjaxResult list(@RequestParam(required = false) final String orderStatus) {
        return AjaxResult.success(orderBizService.list(orderStatus));
    }
}
