package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.user.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.ActivatedImeiForm;
import com.ruoyi.web.form.order.ExcelForm;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.form.order.PlatformImeiForm;
import com.ruoyi.web.vo.order.ImeiQueryVO;
import com.ruoyi.web.vo.order.ImeiRelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j

@RestController
@RequestMapping("apply")
public class IMEIController extends BaseController {

    @Autowired
    private ImeiBizService imeiBizService;


    @PostMapping("/imei/rel")
    public TableDataInfo list(@RequestBody ImeiForm form) {

        PageBO<ImeiRelVO> page = imeiBizService.listRel(form, startParamV2("create_time desc"));

        return getDataTable(page.getData(), page.getTotal());
    }


    @PostMapping("/imei/sum/{status}")
    public AjaxResult count(@PathVariable("status") Integer status) {
        return AjaxResult.success( imeiBizService.count(status));
    }


    @PostMapping("/refuse/{id}")
    public AjaxResult refuse(@PathVariable("id") Long id) {

        imeiBizService.refuse(id);

        return AjaxResult.success();
    }


    @PostMapping("/agree/{id}")
    public AjaxResult agree(@PathVariable("id") Long id) {
        LoginUser loginUser = getLoginUser();
        imeiBizService.agree(id,loginUser.getUserId(), loginUser.getUsername());

        return AjaxResult.success();
    }



    @PostMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {

        imeiBizService.del(id);

        return AjaxResult.success();
    }

    /**
     * 通过平台+品牌+品类查询待发货订单的sn、imei
     */
    @PostMapping("/imei/query")
    public AjaxResult listImeiInfo(@RequestBody ExcelForm excelForm) {
        List<ImeiQueryVO> list = imeiBizService.listImeiInfo(excelForm);
        return AjaxResult.success(list);
    }

    /**
     * 修改平台校验状态并更新订单状态
     */
    @PostMapping("/imei/platform")
    public AjaxResult updatePlatformImei(@Validated @RequestBody PlatformImeiForm form) {
        imeiBizService.updatePlatformImei(form);
        return AjaxResult.success();
    }

    /**
     * 人工放行串码激活状态（仅 NOT_EXITS → SUCCESS）
     */
    @PostMapping("/imei/activated/manual-pass")
    public AjaxResult manualPassActivated(@Validated @RequestBody ActivatedImeiForm form) {
        imeiBizService.manualPassActivated(form);
        return AjaxResult.success();
    }


}
