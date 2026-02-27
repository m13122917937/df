package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.WarehousingOrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.web.form.order.BrandForm;
import com.ruoyi.web.form.order.PickingOrderForm;
import com.ruoyi.web.form.order.WarehousingOrderParam;
import com.ruoyi.web.form.order.WarehousingSaveParam;
import com.ruoyi.web.vo.order.BrandCountVO;
import com.ruoyi.web.vo.order.OrderListVO;
import com.ruoyi.web.vo.order.WarehousingOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Api(tags = "入仓订单")
@RestController
@RequestMapping("/warehousing/order")
public class WarehousingController extends BaseController {


    @Autowired
    WarehousingOrderBizService wOrderBizService;

    @ApiOperation("品牌订单数量")
    @GetMapping("/brand/count")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = BrandCountVO.class)
    })
    public AjaxResult brandCount(BrandForm provinceForm) {

//        ValidatorUtils.validateEntity(provinceForm);

        List<BrandCountVO> brandCountVOS = wOrderBizService.brandCount(provinceForm);

        return AjaxResult.success(brandCountVOS);
    }

    @ApiOperation("查询订单列表")
    @PostMapping("list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = WarehousingOrderVO.class)
    })
    public TableDataInfo list(@RequestBody WarehousingOrderParam warehousingOrderParam) {
        PageBO<WarehousingOrderVO> orderWaitVOPageBO = wOrderBizService.orderList(warehousingOrderParam, startParamV2("create_time desc"));
        return getDataTable(orderWaitVOPageBO.getData(), orderWaitVOPageBO.getTotal());
    }

    @ApiOperation("保存订单")
    @PostMapping("save")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = OrderListVO.class)
    })
    public AjaxResult add(@Validated @RequestBody WarehousingSaveParam warehousingSaveParam) {

        wOrderBizService.save(warehousingSaveParam, getLoginUser());

        return AjaxResult.success();
    }


    @ApiOperation("撤销订单")
    @PostMapping("revoke/{orderCode}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = OrderListVO.class)
    })
    public AjaxResult revoke(@PathVariable("orderCode") String orderCode) {

        wOrderBizService.revoke(orderCode, getLoginUser());

        return AjaxResult.success();
    }

    /**
     * 完成拣货
     *
     * @param orderCode
     * @return
     */
    @ApiOperation("完成拣货")
    @PostMapping("confirm/{orderCode}")
    public AjaxResult confirm(@PathVariable("orderCode") String orderCode) {

        wOrderBizService.confirm(orderCode, getLoginUser());

        return AjaxResult.success();
    }


    /**
     * 完成拣货
     *
     * @param
     * @return
     */
    @ApiOperation("拣货")
    @PostMapping("picking")
    public AjaxResult picking(@Validated() @RequestBody PickingOrderForm pickingOrderForm) throws IOException {

        wOrderBizService.picking(pickingOrderForm, getLoginUser());

        return AjaxResult.success();
    }


}
