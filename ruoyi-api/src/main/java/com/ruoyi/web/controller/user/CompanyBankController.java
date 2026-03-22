package com.ruoyi.web.controller.user;

import com.ruoyi.biz.company.CompanyBankBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.user.model.consts.CompanyBankConsts;
import com.ruoyi.web.form.user.CompanyBankForm;
import com.ruoyi.web.vo.user.CompanyBankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/company/bank")
public class CompanyBankController extends BaseController {

    @Autowired
    CompanyBankBizService companyBankBizService;




    @GetMapping("/list")
    public AjaxResult list() {

        List<CompanyBankVO> list = companyBankBizService.list(getDeptId());

        return AjaxResult.success(list);
    }


    @PostMapping("/save")
    public AjaxResult save(@Validated(AddGroup.class) @RequestBody CompanyBankForm companyBankForm) {
        ValidatorUtils.validateEntity(companyBankForm, AddGroup.class);
        companyBankForm.setCompanyId(getDeptId());
        companyBankForm.setDefaulted(CompanyBankConsts.Defaulted.NO.getValue());
        companyBankBizService.save(companyBankForm, getUserId());

        return AjaxResult.success();
    }



}
