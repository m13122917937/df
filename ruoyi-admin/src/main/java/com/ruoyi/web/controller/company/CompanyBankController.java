package com.ruoyi.web.controller.company;

import com.ruoyi.biz.company.CompanyBankBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.web.form.company.CompanyBankForm;
import com.ruoyi.web.vo.company.CompanyBankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/company/bank")
public class CompanyBankController extends BaseController {

    @Autowired
    CompanyBankBizService companyBankBizService;

    @GetMapping("/list/{companyId}")
    public AjaxResult list(@PathVariable("companyId") Long companyId) {

        List<CompanyBankVO> list = companyBankBizService.list(companyId);

        return AjaxResult.success(list);
    }


    @PostMapping("/save")
    public AjaxResult save(@RequestBody CompanyBankForm companyBankForm) {
        ValidatorUtils.validateEntity(companyBankForm, AddGroup.class);

        companyBankBizService.save(companyBankForm, getUserId());

        return AjaxResult.success();
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody CompanyBankForm companyBankForm) {
        ValidatorUtils.validateEntity(companyBankForm, UpdateGroup.class);

        companyBankBizService.update(companyBankForm, getUserId());

        return AjaxResult.success();
    }

}
