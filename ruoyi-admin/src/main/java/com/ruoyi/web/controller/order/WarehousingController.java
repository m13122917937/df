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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/warehousing/order")
public class WarehousingController extends BaseController {


    @Autowired
    WarehousingOrderBizService wOrderBizService;


    @GetMapping("/brand/count")


    public AjaxResult brandCount(BrandForm provinceForm) {

//        ValidatorUtils.validateEntity(provinceForm);

        List<BrandCountVO> brandCountVOS = wOrderBizService.brandCount(provinceForm);

        return AjaxResult.success(brandCountVOS);
    }


    @PostMapping("list")


    public TableDataInfo list(@RequestBody WarehousingOrderParam warehousingOrderParam) {
        PageBO<WarehousingOrderVO> orderWaitVOPageBO = wOrderBizService.orderList(warehousingOrderParam, startParamV2("create_time desc"));
        return getDataTable(orderWaitVOPageBO.getData(), orderWaitVOPageBO.getTotal());
    }


    @PostMapping("save")


    public AjaxResult add(@Validated @RequestBody WarehousingSaveParam warehousingSaveParam) {

        wOrderBizService.save(warehousingSaveParam, getLoginUser());

        return AjaxResult.success();
    }



    @PostMapping("revoke/{orderCode}")


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

    @PostMapping("picking")
    public AjaxResult picking(@Validated() @RequestBody PickingOrderForm pickingOrderForm) throws IOException {

        wOrderBizService.picking(pickingOrderForm, getLoginUser());

        return AjaxResult.success();
    }


}
