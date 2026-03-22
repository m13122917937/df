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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/bill/payer/config")
public class PayerConfigController extends BaseController {

    @Autowired
    PayerConfigBizService payerConfigBizService;




    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "keyword", required = false) String keyword) {

        PageBO<PayerConfigVO> payerVOPageBO = payerConfigBizService.pageList(keyword, startParamV2("create_time desc"));

        return getDataTable(payerVOPageBO.getData(), payerVOPageBO.getTotal());
    }





    @PostMapping("/update")
    public AjaxResult update(@Validated(value = UpdateGroup.class) @RequestBody PayerConfigForm payerConfigForm) {

        LoginUser loginUser = getLoginUser();

        payerConfigBizService.update(payerConfigForm, loginUser);

        return AjaxResult.success();
    }




    @PostMapping("/save")
    public AjaxResult save(@Validated(value = AddGroup.class) @RequestBody PayerConfigForm payerConfigForm) {

        LoginUser loginUser = getLoginUser();

        payerConfigBizService.save(payerConfigForm, loginUser);

        return AjaxResult.success();
    }




    @PostMapping("/platform")
    public AjaxResult platform() {

        List<String> platform = Arrays.stream(PayerConsts.Platform.values()).map(PayerConsts.Platform::getMessage).collect(Collectors.toList());

        return AjaxResult.success(platform);
    }
}
