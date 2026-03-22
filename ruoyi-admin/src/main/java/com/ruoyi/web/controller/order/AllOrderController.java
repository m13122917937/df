package com.ruoyi.web.controller.order;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.vo.bill.BillPayPlanVO;
import com.ruoyi.web.vo.order.AllOrderVO;
import com.ruoyi.web.vo.order.OrderListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("order/all")
public class AllOrderController extends BaseController {

    @Autowired
    OrderBizService orderBizService;

    @PostMapping("list")
    public TableDataInfo list(@Validated @RequestBody AllOrderForm allOrderForm) {

        PageBO<AllOrderVO> allList = orderBizService.allList(allOrderForm, startParamV2(orderBizService.sortField(null)));

        return getDataTable(allList.getData(), allList.getTotal());
    }

    @PostMapping("export")
    public void export(@Validated @RequestBody AllOrderForm allOrderForm) throws IOException {

        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "订单列表");
        List<AllOrderVO> allOrderVOList = orderBizService.allListExport(allOrderForm);

        EasyExcel.write(response.getOutputStream(), OrderListVO.class).sheet("sheet1").doWrite(allOrderVOList);
    }

}