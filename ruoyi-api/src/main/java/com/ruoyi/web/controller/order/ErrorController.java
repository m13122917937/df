package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ErrorOrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.LogisticsSubscribeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
@RequestMapping("order/error")
public class ErrorController extends BaseController {


    @Autowired
    ErrorOrderBizService errorOrderBizService;


    /**
     * 订阅物流信息并修改订单状态
     */

    @PostMapping("/change")
    public AjaxResult subscribeLogistics(@RequestBody LogisticsSubscribeParam param) {
        errorOrderBizService.subscribeLogisticsAndUpdateOrderStatus(param);
        return AjaxResult.success();
    }


}
