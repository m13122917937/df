package com.ruoyi.web.controller.order;

import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.excel.ExcelPlateformReadListener;
import com.ruoyi.biz.order.ErrorOrderBizService;
import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.biz.order.OrderBizService;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.express.facade.impl.RouteSubscribeFacade;
import com.ruoyi.order.facade.IImeiFacade;
import com.ruoyi.order.facade.IOrderFacade;
import com.ruoyi.order.facade.ITradeOrderFacade;
import com.ruoyi.wangdian.utils.WdtClient;
import com.ruoyi.web.form.order.ErrorOrder;
import com.ruoyi.web.form.order.ExcelForm;
import com.ruoyi.web.vo.order.ExcelPlatformVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;


@RestController
@RequestMapping("/order/error")
public class ErrorOrderController extends BaseController {

    @Autowired
    private OrderBizService orderBizService;

    @Autowired
    private ErrorOrderBizService errorOrderBizService;

    @Autowired
    private ImeiBizService imeiBizService;

    @Autowired
    IImeiFacade imeiFacade;

    @Autowired
    IOrderFacade orderFacade;

    @Autowired
    ITradeOrderFacade tradeOrderFacade;

    @Autowired
    WdtClient wdtClient;

    @Autowired
    RuoYiConfig config;

    @Autowired
    RouteSubscribeFacade routeSubscribeFacade;


    /**
     * 订单转异常
     *
     * @param errorOrder
     * @return
     */

    @GetMapping("error")
    public AjaxResult error(ErrorOrder errorOrder) {
        ValidatorUtils.validateEntity(errorOrder);

        orderBizService.error(errorOrder);

        return AjaxResult.success();
    }


    @GetMapping("error2wait")
    public AjaxResult error2revoke(String orderCode) {
        Assert.notBlank(orderCode, "订单编号不能为空");

        orderBizService.error2Wait(orderCode, getUserId());

        return AjaxResult.success();
    }


    @GetMapping("error2ending")
    public AjaxResult error2Ending(String orderCode, Integer refund) {

        Assert.notBlank(orderCode, "订单编号不能为空");
        Assert.notNull(refund, "请选择是否退还保证金");

        orderBizService.error2Ending(orderCode, getUserId(), refund);

        return AjaxResult.success();
    }


    @PostMapping("/export")
    public void export(@RequestBody ExcelForm excelForm, HttpServletResponse response) throws IOException {

        ServletUtils.renderExcel(response, "待验证列表");
        List<ExcelPlatformVO> excelPlatformVOS = errorOrderBizService.listInfo(excelForm);
        EasyExcel.write(response.getOutputStream(), ExcelPlatformVO.class).sheet("sheet1").doWrite(excelPlatformVOS);

    }


    @PostMapping("/import")
    public AjaxResult importE(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return AjaxResult.success();
        }
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls")) {
            throw new ServerException("请上传excel文件");
        }
        ExcelPlateformReadListener excelPlateformReadListener = new ExcelPlateformReadListener(imeiFacade, orderFacade, wdtClient, config.getWarehouseNo(), tradeOrderFacade, routeSubscribeFacade);
        EasyExcel.read(file.getInputStream(), ExcelPlatformVO.class, excelPlateformReadListener).sheet().doRead();

        return AjaxResult.success();
    }

}
