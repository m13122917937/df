package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.SaleOrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.web.form.order.BrandForm;
import com.ruoyi.web.form.order.PickingOrderForm;
import com.ruoyi.web.form.order.TrackingForm;
import com.ruoyi.web.form.order.WarehousingOrderParam;
import com.ruoyi.web.form.order.WarehousingImportResult;
import com.ruoyi.web.form.order.WarehousingSaveParam;
import com.ruoyi.web.vo.order.BrandCountVO;
import com.ruoyi.web.vo.order.OrderListVO;
import com.ruoyi.web.vo.order.WarehousingOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/sale/order")
public class SaleController extends BaseController {


    @Autowired
    SaleOrderBizService saleOrderBizService;


    @GetMapping("/brand/count")
    public AjaxResult brandCount(BrandForm provinceForm) {

        List<BrandCountVO> brandCountVOS = saleOrderBizService.brandCount(provinceForm);

        return AjaxResult.success(brandCountVOS);
    }


    @PostMapping("list")
    public TableDataInfo list(@RequestBody WarehousingOrderParam warehousingOrderParam) {
        PageBO<WarehousingOrderVO> orderWaitVOPageBO = saleOrderBizService.orderList(warehousingOrderParam, startParamV2("create_time desc"));
        return getDataTable(orderWaitVOPageBO.getData(), orderWaitVOPageBO.getTotal());
    }


    @PostMapping("save")
    public AjaxResult add(@Validated @RequestBody WarehousingSaveParam warehousingSaveParam) {

        saleOrderBizService.save(warehousingSaveParam, getLoginUser());

        return AjaxResult.success();
    }


    @GetMapping("/import/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        saleOrderBizService.downloadTemplate(response);
    }

    @PostMapping("/import/validate")
    public AjaxResult importValidate(@RequestParam("file") MultipartFile file) {
        WarehousingImportResult result = saleOrderBizService.importValidate(file);
        return AjaxResult.success(result);
    }

    @PostMapping("/import")
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file) {
        return saleOrderBizService.importFromExcel(file, getLoginUser());
    }


    @PostMapping("revoke/{orderCode}")
    public AjaxResult revoke(@PathVariable("orderCode") String orderCode) {

        saleOrderBizService.revoke(orderCode, getLoginUser());

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

        saleOrderBizService.confirm(orderCode, getLoginUser());

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

        saleOrderBizService.picking(pickingOrderForm, getLoginUser());

        return AjaxResult.success();
    }

    /**
     * 修改物流信息
     */
    @PostMapping("tracking")
    public AjaxResult updateTracking(@Validated @RequestBody TrackingForm trackingForm) {

        saleOrderBizService.updateTracking(trackingForm);

        return AjaxResult.success();
    }


}
