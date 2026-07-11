package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.web.form.order.RevokeHangingForm;
import com.ruoyi.web.form.order.RevokeOriParam;
import com.ruoyi.web.form.order.RevokeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order/chase")
public class ChaseOrderController extends BaseController {


    @Autowired
    OrderBizService orderBizService;


    @GetMapping("chase2normal")
    public AjaxResult chase2normal(String orderCode) {
        orderBizService.chase2normal(orderCode, getUserId());
        return AjaxResult.success();
    }



    @PostMapping("/revoke")
    public AjaxResult revoke(@RequestBody RevokeParam revokeParam) {
        Assert.notEmpty(revokeParam.getOrderCodeList(), "订单编号参数不能为空");
        logger.info("撤销订单/追单,人为:{}, 参数：{}", getUserId(), JacksonUtil.toJson(revokeParam.getOrderCodeList()));

        orderBizService.revokeList(revokeParam);

        return AjaxResult.success();
    }



    @PostMapping("hanging")
    public AjaxResult revokeHanging(@RequestBody RevokeHangingForm revokeParam) {

        orderBizService.revokeHanging(revokeParam);

        return AjaxResult.success();
    }


}
