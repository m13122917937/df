package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.vo.order.AllOrderVO;
import com.ruoyi.web.vo.order.ImeiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j

@RestController
@RequestMapping("order/all")
public class AllOrderController extends BaseController {


    @Autowired
    private OrderBizService orderBizService;


    @PostMapping("list")
    public TableDataInfo list(@RequestBody AllOrderForm allOrderForm) {
        allOrderForm.setCompanyId(getDeptId());

        PageBO<AllOrderVO> pageBO = orderBizService.allOrderPage(allOrderForm, startParamV2("shipments_time desc"));

        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }


}
