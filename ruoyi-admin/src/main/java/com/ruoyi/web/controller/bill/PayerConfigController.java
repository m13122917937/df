package com.ruoyi.web.controller.bill;

import com.ruoyi.bill.constant.PayerConsts;
import com.ruoyi.biz.bill.PayerConfigBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.web.form.bill.PayerConfigForm;
import com.ruoyi.web.vo.bill.PayerConfigVO;
import com.ruoyi.web.vo.company.CompanyBankVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "付款配置")
@RequestMapping("/bill/payer/config")
public class PayerConfigController extends BaseController {

    @Autowired
    PayerConfigBizService payerConfigBizService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = PayerConfigVO.class)
    })
    @ApiOperation("列表")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "keyword", required = false) String keyword) {

        PageBO<PayerConfigVO> payerVOPageBO = payerConfigBizService.pageList(keyword, startParamV2("create_time desc"));

        return getDataTable(payerVOPageBO.getData(), payerVOPageBO.getTotal());
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ApiOperation("更新")
    @PostMapping("/update")
    public AjaxResult update(@Validated(value = UpdateGroup.class) @RequestBody PayerConfigForm payerConfigForm) {

        LoginUser loginUser = getLoginUser();

        payerConfigBizService.update(payerConfigForm, loginUser);

        return AjaxResult.success();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ApiOperation("保存")
    @PostMapping("/save")
    public AjaxResult save(@Validated(value = AddGroup.class) @RequestBody PayerConfigForm payerConfigForm) {

        LoginUser loginUser = getLoginUser();

        payerConfigBizService.save(payerConfigForm, loginUser);

        return AjaxResult.success();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功")
    })
    @ApiOperation("平台列表")
    @PostMapping("/platform")
    public AjaxResult platform() {

        List<String> platform = Arrays.stream(PayerConsts.Platform.values()).map(PayerConsts.Platform::getMessage).collect(Collectors.toList());

        return AjaxResult.success(platform);
    }
}
