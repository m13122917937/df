package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.TradeBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.web.form.order.TradePriceForm;
import com.ruoyi.web.vo.order.TradePriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    TradeBizService tradeBizService;


    @GetMapping("list/today")


    public AjaxResult today(TradePriceForm tradePriceForm) {

        List<TradePriceVO> today = tradeBizService.today(tradePriceForm);

        return AjaxResult.success(today);
    }


    @GetMapping("list/yesterday")


    public AjaxResult yesterday(TradePriceForm tradePriceForm) {
        ValidatorUtils.validateEntity(tradePriceForm);

        List<TradePriceVO> yesterday = tradeBizService.yesterday(tradePriceForm);


        return AjaxResult.success(yesterday);
    }

}
