package com.ruoyi.web.controller.bill;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.web.form.bill.ConfirmSplitForm;
import com.ruoyi.web.form.bill.SplitForm;
import com.ruoyi.web.vo.bill.BillSumVO;
import com.ruoyi.web.vo.bill.BillVO;
import com.ruoyi.web.vo.bill.PaySummaryVO;
import com.ruoyi.web.vo.bill.SumBillVO;
import com.ruoyi.web.vo.company.CompanyBankVO;
import com.ruoyi.web.vo.order.OrderDeliveryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller

@RequestMapping("/bill/detail")
public class BillController extends BaseController {

    @Autowired
    BillBizService billBizService;

    @ResponseBody
    @GetMapping("today/sum")
    public AjaxResult sum(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                          @RequestParam(value = "supplierId", required = false) Long supplierId,
                          @RequestParam(value = "billType", required = true) Integer billType) {

        SumBillVO sumBillVO = billBizService.querySum(payCompanyId, supplierId, billType);

        return AjaxResult.success(sumBillVO);
    }

    @GetMapping("today")
    @ResponseBody
    public TableDataInfo info(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                              @RequestParam(value = "supplierId", required = false) Long supplierId,
                              @RequestParam(value = "billType", required = true) Integer billType) {

        PageBO<BillSumVO> billSumVOPageBO = billBizService.queryBillSUM(payCompanyId, supplierId, billType, startParamV2());

        return getDataTable(billSumVOPageBO.getData(), billSumVOPageBO.getTotal());
    }


    @ResponseBody
    @GetMapping("detail")
    public TableDataInfo queryBill(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                                   @RequestParam(value = "supplierId", required = true) Long supplierId,
                                   @RequestParam(value = "billType", required = true) Integer billType) {

        PageBO<BillVO> billVOPageBO = billBizService.pageList(payCompanyId, supplierId, billType, startParamV2());

        return getDataTable(billVOPageBO.getData(), billVOPageBO.getTotal());
    }

    @GetMapping("detail/export")
    public void queryBillExport(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                                   @RequestParam(value = "supplierId", required = false) Long supplierId,
                                   @RequestParam(value = "billType", required = true) Integer billType) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "付款详情");
        List<BillVO> billVOS = billBizService.queryBillExport(payCompanyId, supplierId, billType);
        EasyExcel.write(response.getOutputStream(), BillVO.class).sheet("sheet1").doWrite(billVOS);

    }


    @ResponseBody
    @PostMapping("split")
    public AjaxResult split(@RequestBody SplitForm splitForm) {

        PaySummaryVO split = billBizService.split(splitForm);

        return AjaxResult.success(split);
    }

    @ResponseBody
    @PostMapping("plan")
    public AjaxResult plan(@Validated @RequestBody ConfirmSplitForm confirmSplitForm) {

        billBizService.plan(confirmSplitForm, getLoginUser());

        return AjaxResult.success();
    }

    @ResponseBody
    @PostMapping("revoke/{planId}")
    public AjaxResult revoke(@PathVariable("planId") Long planId) {

        billBizService.revoke(planId, getLoginUser());

        return AjaxResult.success();
    }


}
