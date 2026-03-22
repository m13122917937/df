package com.ruoyi.web.controller.bill;

import com.ruoyi.biz.bill.PayerBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.web.form.bill.PayerForm;
import com.ruoyi.web.vo.bill.PayerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/bill/payer")
public class PayerController extends BaseController {

    @Autowired
    PayerBizService payerBizService;




    @GetMapping("/page/list")
    public TableDataInfo pageList(@RequestParam(value = "payName", required = false) String payName,
                                  @RequestParam(value = "actived" , required = false)Integer actived ) {

        PageBO<PayerVO> payerVOPageBO = payerBizService.pageList(actived,payName, startParamV2("create_time desc"));

        return getDataTable(payerVOPageBO.getData(), payerVOPageBO.getTotal());
    }





    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "payName", required = false) String payName) {

        List<PayerVO> payerVOPageBO = payerBizService.list(payName);

        return AjaxResult.success(payerVOPageBO);
    }





    @PostMapping("/update")
    public AjaxResult update(@Validated(value = UpdateGroup.class) @RequestBody PayerForm payerForm) {

        LoginUser loginUser = getLoginUser();
        payerForm.setBalance(null);
        payerBizService.update(payerForm, loginUser);

        return AjaxResult.success();
    }




    @PostMapping("/save")
    public AjaxResult save(@Validated(value = AddGroup.class) @RequestBody PayerForm payerForm) {

        LoginUser loginUser = getLoginUser();

        payerBizService.save(payerForm, loginUser);

        return AjaxResult.success();
    }


}
