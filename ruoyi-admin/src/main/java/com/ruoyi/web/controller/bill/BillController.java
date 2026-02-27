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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@Api(tags = "今日付款")
@RequestMapping("/bill/detail")
public class BillController extends BaseController {

    @Autowired
    BillBizService billBizService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = SumBillVO.class)
    })
    @ResponseBody
    @ApiOperation("今天待付款汇总数据")
    @GetMapping("today/sum")
    public AjaxResult sum(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                          @RequestParam(value = "supplierId", required = false) Long supplierId,
                          @RequestParam(value = "billType", required = true) Integer billType) {

        SumBillVO sumBillVO = billBizService.querySum(payCompanyId, supplierId, billType);

        return AjaxResult.success(sumBillVO);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = BillSumVO.class)
    })
    @ApiOperation("今天待付款汇总分页列表")
    @GetMapping("today")
    @ResponseBody
    public TableDataInfo info(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                              @RequestParam(value = "supplierId", required = false) Long supplierId,
                              @RequestParam(value = "billType", required = true) Integer billType) {

        PageBO<BillSumVO> billSumVOPageBO = billBizService.queryBillSUM(payCompanyId, supplierId, billType, startParamV2());

        return getDataTable(billSumVOPageBO.getData(), billSumVOPageBO.getTotal());
    }



    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = BillVO.class)
    })
    @ResponseBody
    @ApiOperation("查询待付款分页列表详情")
    @GetMapping("detail")
    public TableDataInfo queryBill(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                                   @RequestParam(value = "supplierId", required = true) Long supplierId,
                                   @RequestParam(value = "billType", required = true) Integer billType) {

        PageBO<BillVO> billVOPageBO = billBizService.pageList(payCompanyId, supplierId, billType, startParamV2());

        return getDataTable(billVOPageBO.getData(), billVOPageBO.getTotal());
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = BillVO.class)
    })
    @ApiOperation("查询待付款详情导出")
    @GetMapping("detail/export")
    public void queryBillExport(@RequestParam(value = "payCompanyId", required = false) Long payCompanyId,
                                   @RequestParam(value = "supplierId", required = false) Long supplierId,
                                   @RequestParam(value = "billType", required = true) Integer billType) throws IOException {
        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, "付款详情");
        List<BillVO> billVOS = billBizService.queryBillExport(payCompanyId, supplierId, billType);
        EasyExcel.write(response.getOutputStream(), BillVO.class).sheet("sheet1").doWrite(billVOS);

    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PaySummaryVO.class)
    })
    @ResponseBody
    @ApiOperation("通过订单号，查询付款企业主体")
    @PostMapping("split")
    public AjaxResult split(@RequestBody SplitForm splitForm) {

        PaySummaryVO split = billBizService.split(splitForm);

        return AjaxResult.success(split);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ResponseBody
    @ApiOperation("确定排款")
    @PostMapping("plan")
    public AjaxResult plan(@Validated @RequestBody ConfirmSplitForm confirmSplitForm) {

        billBizService.plan(confirmSplitForm, getLoginUser());

        return AjaxResult.success();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ResponseBody
    @ApiOperation("撤销排款")
    @PostMapping("revoke/{planId}")
    public AjaxResult revoke(@PathVariable("planId") Long planId) {

        billBizService.revoke(planId, getLoginUser());

        return AjaxResult.success();
    }


}
