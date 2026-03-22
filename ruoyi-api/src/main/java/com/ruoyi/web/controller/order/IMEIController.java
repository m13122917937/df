package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.ImeiBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.order.ImeiOrderParam;
import com.ruoyi.web.vo.order.ImeiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("imei")
public class IMEIController {

    @Autowired
    private ImeiBizService imeiBizService;


    @PostMapping("/save")


    public AjaxResult imei(@RequestBody ImeiOrderParam imeiOrderParam) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        // 验证串码
        List<ImeiVO> imeiVOS = imeiBizService.verifyImei(imeiOrderParam);
        // 查询串码验证状态
        return AjaxResult.success(imeiVOS);
    }


    @GetMapping("/{orderCode}")


    public AjaxResult list(@PathVariable("orderCode") String orderCode) {
        List<ImeiVO> imeiVOS = imeiBizService.list(orderCode);
        return AjaxResult.success(imeiVOS);
    }



}
