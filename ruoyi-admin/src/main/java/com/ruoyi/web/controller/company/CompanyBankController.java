package com.ruoyi.web.controller.company;

import com.ruoyi.biz.company.CompanyBankBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.web.form.company.CompanyBankForm;
import com.ruoyi.web.vo.company.CompanyBankVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "企业银行卡相关")
@RequestMapping("/company/bank")
public class CompanyBankController extends BaseController {

    @Autowired
    CompanyBankBizService companyBankBizService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = CompanyBankVO.class)
    })
    @ApiOperation("查询企业银行卡列表")
    @GetMapping("/list/{companyId}")
    public AjaxResult list(@PathVariable("companyId") Long companyId) {

        List<CompanyBankVO> list = companyBankBizService.list(companyId);

        return AjaxResult.success(list);
    }

    @ApiOperation("保存企业银行卡")
    @PostMapping("/save")
    public AjaxResult save(@RequestBody CompanyBankForm companyBankForm) {
        ValidatorUtils.validateEntity(companyBankForm, AddGroup.class);

        companyBankBizService.save(companyBankForm, getUserId());

        return AjaxResult.success();
    }


    @ApiOperation("修改企业银行卡")
    @PutMapping("/update")
    public AjaxResult update(@RequestBody CompanyBankForm companyBankForm) {
        ValidatorUtils.validateEntity(companyBankForm, UpdateGroup.class);

        companyBankBizService.update(companyBankForm, getUserId());

        return AjaxResult.success();
    }


}
