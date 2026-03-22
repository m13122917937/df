package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.vo.order.ImeiRelVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        imeiBizService.agree(id);

        return AjaxResult.success();
    }



    @PostMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {

        imeiBizService.del(id);

        return AjaxResult.success();
    }


}
