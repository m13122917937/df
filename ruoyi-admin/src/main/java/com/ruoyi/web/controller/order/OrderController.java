package com.ruoyi.web.controller.order;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.excel.ExcelOrderReadListener;
import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.JacksonUtil;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.order.model.consts.OrderConsts;
import com.ruoyi.rule.model.consts.RuleConsts;
import com.ruoyi.web.form.order.BrandForm;
import com.ruoyi.web.form.order.OrderAddForm;
import com.ruoyi.web.form.order.OrderNewForm;
import com.ruoyi.web.form.order.ProvinceForm;
import com.ruoyi.web.form.rule.RuleForm;
import com.ruoyi.web.vo.order.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("/order/new")
public class OrderController extends BaseController {

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private ImeiBizService imeiBizService;


    /**
     * 上传文件
     */
    @Anonymous
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws Exception {

        checkExcel(file);

        ExcelOrderReadListener excelPlateformReadListener = new ExcelOrderReadListener(orderBizService);
        EasyExcel.read(file.getInputStream(), OrderAddForm.class, excelPlateformReadListener).sheet().doRead();

        return AjaxResult.success();
    }

    @Anonymous
    @PostMapping("add")
    public AjaxResult add(@Validated(value = AddGroup.class) @RequestBody OrderAddForm orderAddForm) {

        log.info("添加订单，订单信息:{}", JacksonUtil.toJson(orderAddForm));

        orderBizService.add(orderAddForm);

        return AjaxResult.success();
    }


    @PostMapping("list")


    public TableDataInfo list(@RequestBody OrderNewForm orderNewParam) {
        orderNewParam.setOrderType(OrderConsts.OrderType.O2O.getCode());
        PageBO<OrderListVO> orderWaitVOPageBO = orderBizService.orderList(orderNewParam, startParamV2(orderBizService.sortField(orderNewParam.getStatus())));
        return getDataTable(orderWaitVOPageBO.getData(), orderWaitVOPageBO.getTotal());
    }


    @PostMapping("send/list")
    public TableDataInfo sendList(@RequestBody OrderNewForm orderNewParam) {
        orderNewParam.setOrderType(OrderConsts.OrderType.O2O.getCode());
        PageBO<OrderListVO> orderWaitVOPageBO = orderBizService.orderSendList(orderNewParam, startParamV2(orderBizService.sortField(orderNewParam.getStatus())));
        return getDataTable(orderWaitVOPageBO.getData(), orderWaitVOPageBO.getTotal());
    }


    @PostMapping("list/export")
    public void listExport(@RequestBody OrderNewForm orderNewParam) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "订单列表");
        orderNewParam.setOrderType(OrderConsts.OrderType.O2O.getCode());
        List<OrderListVO> orderListExport = orderBizService.orderListExport(orderNewParam);
        EasyExcel.write(response.getOutputStream(), OrderListVO.class).sheet("sheet1").doWrite(orderListExport);
    }



    @GetMapping("list/{orderId}/imei")
    public AjaxResult imei(@PathVariable("orderId") String orderCode) {

        List<ImeiVO> list = imeiBizService.list(orderCode);

        return AjaxResult.success(list);
    }



    @PostMapping("quotation")
    public AjaxResult quotation(@RequestBody RuleForm ruleParam) {
        ValidatorUtils.validateEntity(ruleParam, AddGroup.class);
        ruleParam.setRuleRange(RuleConsts.Range.COUNTY.getCode());

        orderBizService.quotation(ruleParam, getUserId());

        return AjaxResult.success();
    }



    @GetMapping("/province/count")


    public AjaxResult provinceCount(ProvinceForm provinceForm) {

        ValidatorUtils.validateEntity(provinceForm);

        List<OrderAreaCountVO> orderAreaCountVOS = orderBizService.provinceCount(provinceForm);

        return AjaxResult.success(orderAreaCountVOS);
    }



    @GetMapping("/brand/count")
    public AjaxResult brandCount(BrandForm provinceForm) {

        ValidatorUtils.validateEntity(provinceForm);

        List<BrandCountVO> brandCountVOS = orderBizService.brandCount(provinceForm);

        return AjaxResult.success(brandCountVOS);
    }



    @GetMapping("/ending/confirm")
    public AjaxResult success(String orderCode) {

        orderBizService.ending(orderCode, getUserId());

        return AjaxResult.success();
    }


}
