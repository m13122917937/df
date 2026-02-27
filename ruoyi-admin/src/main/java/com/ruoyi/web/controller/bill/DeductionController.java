package com.ruoyi.web.controller.bill;

import com.ruoyi.bill.model.bo.DeductionBO;
import com.ruoyi.bill.model.bo.TransactionsBO;
import com.ruoyi.bill.model.query.DeductionQuery;
import com.ruoyi.biz.bill.DeductionBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.mapper.bill.DeductionConvert;
import com.ruoyi.web.form.bill.DeductionListForm;
import com.ruoyi.web.form.bill.DeductionSaveForm;
import com.ruoyi.web.vo.bill.DeductionInfoVO;
import com.ruoyi.web.vo.bill.DeductionVO;
import com.ruoyi.web.vo.bill.SumBillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "扣罚")
@RequestMapping("/bill/deduction")
public class DeductionController extends BaseController {

    @Autowired
    DeductionBizService deductionBizService;

    @PostMapping("/list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = DeductionVO.class)
    })
    public TableDataInfo pageList(@RequestBody @Validated DeductionListForm deductionListForm) {
        DeductionQuery deductionQuery = DeductionConvert.INSTANCE.toQueryParam(deductionListForm);

        PageBO<DeductionBO> deductionBOPageBO = deductionBizService.listPage(deductionQuery, startParamV2("create_time desc"));

        return getDataTable(DeductionConvert.INSTANCE.toVOList(deductionBOPageBO.getData()), deductionBOPageBO.getTotal());
    }

    @ApiOperation("保存扣罚")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody @Validated DeductionSaveForm deductionSaveForm) {

        deductionBizService.save(deductionSaveForm, getLoginUser());

        return AjaxResult.success();
    }

    @ApiOperation("订单详情")
    @GetMapping("/{orderCode}")
    public AjaxResult delete(@PathVariable("orderCode")String orderCode) {
        DeductionInfoVO info = deductionBizService.info(orderCode);
        return AjaxResult.success(info);
    }

    @ApiOperation("撤销扣罚")
    @GetMapping("/revoke/{orderCode}")
    public AjaxResult revoke(@PathVariable("orderCode")String orderCode) {
        deductionBizService.revoke(orderCode);
        return AjaxResult.success();
    }

}
