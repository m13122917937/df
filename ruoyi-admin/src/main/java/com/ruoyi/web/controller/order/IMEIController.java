package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.web.form.order.ImeiForm;
import com.ruoyi.web.vo.order.ImeiRelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "串码")
@RestController
@RequestMapping("apply")
public class IMEIController extends BaseController {

    @Autowired
    private ImeiBizService imeiBizService;

    @ApiOperation("list")
    @PostMapping("/imei/rel")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ImeiRelVO.class)
    })
    public TableDataInfo list(@RequestBody ImeiForm form) {

        PageBO<ImeiRelVO> page = imeiBizService.listRel(form, startParamV2("create_time desc"));

        return getDataTable(page.getData(), page.getTotal());
    }

    @ApiOperation("查询待审核数量")
    @PostMapping("/imei/sum/{status}")
    public AjaxResult count(@PathVariable("status") Integer status) {
        return AjaxResult.success( imeiBizService.count(status));
    }

    @ApiOperation("拒绝")
    @PostMapping("/refuse/{id}")
    public AjaxResult refuse(@PathVariable("id") Long id) {

        imeiBizService.refuse(id);

        return AjaxResult.success();
    }

    @ApiOperation("同意")
    @PostMapping("/agree/{id}")
    public AjaxResult agree(@PathVariable("id") Long id) {

        imeiBizService.agree(id);

        return AjaxResult.success();
    }


    @ApiOperation("删除")
    @PostMapping("/del/{id}")
    public AjaxResult del(@PathVariable("id") Long id) {

        imeiBizService.del(id);

        return AjaxResult.success();
    }


}
