package com.ruoyi.web.controller.order;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.ruoyi.biz.order.RuleBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.common.validator.group.AddGroup;
import com.ruoyi.common.validator.group.UpdateGroup;
import com.ruoyi.mapper.rule.RuleConvert;
import com.ruoyi.rule.model.bo.RuleBO;
import com.ruoyi.rule.model.consts.RuleConsts;
import com.ruoyi.rule.model.param.RuleParam;
import com.ruoyi.rule.model.query.RuleQuery;
import com.ruoyi.web.form.rule.RuleForm;
import com.ruoyi.web.form.rule.RuleListForm;
import com.ruoyi.web.form.rule.RuleQueryForm;
import com.ruoyi.web.vo.order.RuleVO;
import com.ruoyi.web.vo.order.SKURuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/rule")
public class RuleController extends BaseController {

    @Autowired
    private RuleBizService ruleBizService;


    @GetMapping("list")
    public TableDataInfo list(RuleQueryForm ruleQueryForm) {
        DateTime dateTime = DateUtil.yesterday().setField(DateField.HOUR, 18).setField(DateField.MINUTE, 0);

        PageBO<RuleVO> ruleVOS = ruleBizService.listPage(ruleQueryForm.setGtCreateTime(dateTime), startParamV2());

        return getDataTable(ruleVOS.getData(), ruleVOS.getTotal());
    }


    @GetMapping("/sku/list")
    public AjaxResult skuList() {
        DateTime dateTime = DateUtil.yesterday().setField(DateField.HOUR, 18).setField(DateField.MINUTE, 0);

        List<RuleBO> ruleBOS = ruleBizService.ruleFacade.distinctSku(dateTime);
        List<SKURuleVO> ruleVOS = RuleConvert.INSTANCE.toSKUVo(ruleBOS);
        return AjaxResult.success(ruleVOS);
    }


    @PutMapping("disable")
    public AjaxResult disable(@RequestBody RuleListForm ruleListForm) {

        Assert.notEmpty(ruleListForm.getRuleIdList(), "参数不能为空");

        ruleBizService.ruleFacade.update(new RuleParam().setStatus(RuleConsts.Status.FAILURE.getCode()), new RuleQuery().setIdList(ruleListForm.getRuleIdList()));

        return super.success();
    }


    @PutMapping("enable")
    public AjaxResult enable(@RequestBody RuleListForm ruleListForm) {
        Assert.notEmpty(ruleListForm.getRuleIdList(), "参数不能为空");

        ruleBizService.ruleFacade.update(new RuleParam().setStatus(RuleConsts.Status.NORMAL.getCode()), new RuleQuery().setIdList(ruleListForm.getRuleIdList()));

        return super.success();
    }


    @DeleteMapping("del")
    public AjaxResult del(@RequestBody RuleListForm ruleListForm) {

        Assert.notEmpty(ruleListForm.getRuleIdList(), "参数不能为空");

        ruleBizService.ruleFacade.delete(new RuleQuery().setIdList(ruleListForm.getRuleIdList()));

        return AjaxResult.success();
    }


    @PostMapping("save")
    public AjaxResult save(@RequestBody RuleForm ruleParam) {
        ValidatorUtils.validateEntity(ruleParam, AddGroup.class);

        RuleParam param = RuleConvert.INSTANCE.toParam(ruleParam).setCreateTime(DateUtil.date()).setCreateBy(getUserId()).setStatus(RuleConsts.Status.NORMAL.getCode());
        RuleBO ruleBO = ruleBizService.saveOrUpdate(param);
        ruleBizService.execute(ruleBO);

        return AjaxResult.success();
    }



    @PutMapping("update")
    public AjaxResult update(@RequestBody RuleForm ruleParam) {
        ValidatorUtils.validateEntity(ruleParam, UpdateGroup.class);
        Long userId = getUserId();
        RuleParam param = RuleConvert.INSTANCE.toParam(ruleParam).setCreateTime(DateUtil.date()).setCreateBy(userId).setUpdateBy(userId).setStatus(RuleConsts.Status.NORMAL.getCode());
        RuleBO ruleBO = ruleBizService.saveOrUpdate(param);
        ruleBizService.execute(ruleBO);

        return AjaxResult.success();
    }


    @GetMapping("/brand/list")
    public AjaxResult brand() {
        DateTime dateTime = DateUtil.yesterday().setField(DateField.HOUR, 18).setField(DateField.MINUTE, 0);

        List<String> brandList = ruleBizService.ruleFacade.brandList(dateTime);

        return AjaxResult.success(brandList);
    }



    @GetMapping("/category/list")
    public AjaxResult category() {
        DateTime dateTime = DateUtil.yesterday().setField(DateField.HOUR, 18).setField(DateField.MINUTE, 0);

        List<String> brandList = ruleBizService.ruleFacade.categoryList(dateTime);

        return AjaxResult.success(brandList);
    }


}
