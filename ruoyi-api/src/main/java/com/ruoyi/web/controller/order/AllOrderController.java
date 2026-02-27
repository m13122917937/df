package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.AllOrderForm;
import com.ruoyi.web.vo.order.AllOrderVO;
import com.ruoyi.web.vo.order.ImeiVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "全部订单接口")
@RestController
@RequestMapping("order/all")
public class AllOrderController extends BaseController {


    @Autowired
    private OrderBizService orderBizService;

    @ApiOperation("全部订单")
    @PostMapping("list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = AllOrderVO.class)
    })
    public TableDataInfo list(@RequestBody AllOrderForm allOrderForm) {
        allOrderForm.setCompanyId(getDeptId());

        PageBO<AllOrderVO> pageBO = orderBizService.allOrderPage(allOrderForm, startParamV2("shipments_time desc"));

        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }


}
