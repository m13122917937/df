package com.ruoyi.web.controller.bill;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.bill.BillBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.web.form.bill.BillForm;
import com.ruoyi.web.util.ExcelUtil;
import com.ruoyi.web.vo.bill.BillExportVO;
import com.ruoyi.web.vo.bill.TodayBillPayPlanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;

@Slf4j

@RestController
@Anonymous
@RequestMapping("/bill/today")
public class BillController extends BaseController {

    @Autowired
    BillBizService billBizService;




    @PostMapping("/list")
    public TableDataInfo list(@RequestBody BillForm billForm) {

        billForm.setSupplierId(getDeptId());

        PageBO<TodayBillPayPlanVO> billPayPlanVOPageBO = billBizService.pageList(billForm, startParamV2("create_time desc"));

        return getDataTable(billPayPlanVOPageBO.getData(), billPayPlanVOPageBO.getTotal());
    }



    @GetMapping("/confirm/{id}")
    public AjaxResult confirm(@PathVariable("id") Long id) {

        billBizService.confirm(id, getDeptId());

        return AjaxResult.success();
    }


    @GetMapping("/error/{id}")
    public AjaxResult error(@PathVariable("id") Long id) {

        billBizService.fail(id, getDeptId());

        return AjaxResult.success();
    }


    @GetMapping("/export/{id}")
    public void export(@PathVariable("id") Long planId) throws IOException {

        HttpServletResponse response = ServletUtils.getResponse();
        ServletUtils.renderExcel(response, DateUtil.format(DateUtil.date(), NORM_DATETIME_PATTERN));
        ExcelUtil.write(response.getOutputStream(), "今日付款", billBizService.export(planId, getDeptId()), BillExportVO.class);

    }

}
