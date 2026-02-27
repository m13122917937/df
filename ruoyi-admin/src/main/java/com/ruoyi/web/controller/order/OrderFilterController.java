package com.ruoyi.web.controller.order;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.OrderFilter;
import com.ruoyi.web.vo.order.OrderStatusVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Api(tags = "订单筛选接口")
@RestController
@RequestMapping("/order/filter")
public class OrderFilterController {

    @Autowired
    OrderBizService orderBizService;


    @ApiOperation("头部数量")
    @PostMapping("countHeader")
    public AjaxResult countHeader(){
        DateTime dateTime = DateUtil.offsetDay(DateUtil.date(), -15);

        List<OrderStatusVO> orderStatusVOS = orderBizService.countHeader(dateTime);

        return AjaxResult.success(orderStatusVOS);
    }


}
