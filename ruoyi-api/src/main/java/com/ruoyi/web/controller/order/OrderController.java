package com.ruoyi.web.controller.order;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.excel.DropdownWriteHandler;
import com.ruoyi.biz.order.AgeBizService;
import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.system.model.consts.DictDataConsts;
import com.ruoyi.web.form.order.OrderForm;
import com.ruoyi.web.vo.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@RestController
@RequestMapping("order")
public class OrderController extends BaseController {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    ImeiBizService imeiBizService;

    @Autowired
    AgeBizService ageBizService;


    @PostMapping("delivery/ing/list")
    public TableDataInfo deliveryIng(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.DELIVERY_ING.getCode()));
        orderForm.setCompanyId(getDeptId());
        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("delivery/ing/list/export")
    public void deliveryIngExport(@RequestBody OrderForm orderForm) throws IOException {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.DELIVERY_ING.getCode()));
        orderForm.setCompanyId(getDeptId());
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "订单列表");

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, null);
        orderBizService.defaultExpress(pageBO);
        List<SysDictData> dictCache = DictUtils.getDictCache(DictDataConsts.P_EXPRESS_COMPANY);
        List<String> expressCompany = dictCache.stream().map(SysDictData::getDictLabel).collect(Collectors.toList());
        EasyExcel.write(response.getOutputStream(), OrderVO.class).registerWriteHandler(new DropdownWriteHandler(16, expressCompany)).sheet("sheet1").doWrite(pageBO.getData());
    }

    @PostMapping("delivery/ing/list/import")
    public AjaxResult deliveryImport(@RequestParam("file") MultipartFile file) throws IOException {

        checkExcel(file);

        ageBizService.importDelivery(file);

        return AjaxResult.success();
    }

    @PostMapping("delivery/end/list")
    public TableDataInfo deliveryEnd(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.DELIVERY_END.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("delivery/end/list/export")
    public void deliveryEndExport(@RequestBody OrderForm orderForm) throws IOException {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.DELIVERY_END.getCode()));
        orderForm.setCompanyId(getDeptId());
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "订单列表");

        List<DeliveryEndOrderVO> orderVOS = orderBizService.deliveryEndExport(orderForm);

        EasyExcel.write(response.getOutputStream(), DeliveryEndOrderVO.class).sheet("sheet1").doWrite(orderVOS);

    }

    @PostMapping("transit/list")
    public TableDataInfo transit(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.TRANSIT.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("chase/list")
    public TableDataInfo chase(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.CHASE_ORDER.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("error/list")
    public TableDataInfo errorList(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.ERROR.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("receipt/list")
    public TableDataInfo ending(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.ENDING.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @PostMapping("afterSales/list")
    public TableDataInfo afterSales(@RequestBody OrderForm orderForm) {
        orderForm.setStatusList(List.of(OrderConsts.OrderStatus.AFTER_SALES.getCode()));
        orderForm.setCompanyId(getDeptId());

        PageBO<OrderVO> pageBO = orderBizService.pageList(orderForm, startParamV2());
        return getDataTable(pageBO.getData(), pageBO.getTotal());
    }

    @GetMapping("province/{status}")
    public AjaxResult provinceCount(@PathVariable("status") Integer status, @RequestParam(value = "brand", required = false) String brand) {

        List<OrderAreaCountVO> orderAreaCountVOS = orderBizService.provinceCount(status, getDeptId(), brand);

        return AjaxResult.success(orderAreaCountVOS);
    }


    @GetMapping("brand/{status}")
    public AjaxResult brandCount(@PathVariable("status") Integer status, @RequestParam(value = "province", required = false) Long province) {

        List<OrderBrandCountVO> orderAreaCountVOS = orderBizService.brandCount(status, getDeptId(), province);

        return AjaxResult.success(orderAreaCountVOS);
    }



    @GetMapping("list/{orderId}/imei")
    public AjaxResult imei(@PathVariable("orderId") String orderCode) {

        List<ImeiVO> list = imeiBizService.list(orderCode);

        return AjaxResult.success(list);
    }


    @GetMapping("confirmRevoke/{orderCode}")
    public AjaxResult confirmRevoke(@PathVariable("orderCode") String orderCode) {
        orderBizService.confirmRevoke(orderCode);
        return AjaxResult.success();
    }


}
