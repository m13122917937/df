package com.ruoyi.web.controller.order;

import com.ruoyi.biz.order.TradeBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.validator.ValidatorUtils;
import com.ruoyi.web.form.order.TradePriceForm;
import com.ruoyi.web.vo.order.TradePriceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "交易数据")
@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    TradeBizService tradeBizService;

    @ApiOperation("查询今天成交数据")
    @GetMapping("list/today")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = TradePriceVO.class)
    })
    public AjaxResult today(TradePriceForm tradePriceForm) {

        List<TradePriceVO> today = tradeBizService.today(tradePriceForm);

        return AjaxResult.success(today);
    }

    @ApiOperation("查询昨天天成交数据")
    @GetMapping("list/yesterday")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", response = TradePriceVO.class)
    })
    public AjaxResult yesterday(TradePriceForm tradePriceForm) {
        ValidatorUtils.validateEntity(tradePriceForm);

        List<TradePriceVO> yesterday = tradeBizService.yesterday(tradePriceForm);


        return AjaxResult.success(yesterday);
    }

}
