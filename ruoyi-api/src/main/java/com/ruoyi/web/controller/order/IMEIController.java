package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.ImeiOrderParam;
import com.ruoyi.web.vo.order.ImeiVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Api(tags = "串码")
@RestController
@RequestMapping("imei")
public class IMEIController {

    @Autowired
    private ImeiBizService imeiBizService;

    @ApiOperation("填写imei码")
    @PostMapping("/save")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ImeiVO.class)
    })
    public AjaxResult imei(@RequestBody ImeiOrderParam imeiOrderParam) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // 验证串码
        List<ImeiVO> imeiVOS = imeiBizService.verifyImei(imeiOrderParam);
        // 查询串码验证状态
        return AjaxResult.success(imeiVOS);
    }

    @ApiOperation("查看订单串码")
    @GetMapping("/{orderCode}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = ImeiVO.class)
    })
    public AjaxResult list(@PathVariable("orderCode") String orderCode) {
        List<ImeiVO> imeiVOS = imeiBizService.list(orderCode);
        return AjaxResult.success(imeiVOS);
    }



}
