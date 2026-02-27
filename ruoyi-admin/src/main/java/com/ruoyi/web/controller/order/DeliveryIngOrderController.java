package com.ruoyi.web.controller.order;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.web.form.order.OrderNewForm;
import com.ruoyi.web.vo.order.OrderDeliveryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(tags = "当日发货controller")
@RestController
@RequestMapping("/order/delivery")
public class DeliveryIngOrderController {

    @Autowired
    OrderBizService orderBizService;

    @ApiOperation("当日发货订单导出")
    @PostMapping("export")
    public void export(@RequestBody OrderNewForm orderNewParam) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        List<OrderDeliveryVO> orderDeliveryVOS = orderBizService.orderList(orderNewParam);
        ServletUtils.renderExcel(response, "当日发货订单");
        EasyExcel.write(response.getOutputStream(), OrderDeliveryVO.class).sheet("sheet1").doWrite(orderDeliveryVOS);
    }
}
