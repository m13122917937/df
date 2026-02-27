package com.ruoyi.web.controller.open;


import com.ruoyi.biz.open.OpenBizService;
import com.ruoyi.biz.order.OrderAddressBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.form.open.OpenForm;
import com.ruoyi.web.form.order.AddressCompletedParams;
import com.ruoyi.web.vo.open.PddShopVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@Api(tags = "开放平台接口")
@RestController
@RequestMapping("/open/order")
public class OpenController {

    @Autowired
    OpenBizService openBizService;

    @Autowired
    OrderAddressBizService orderAddressBizService;

    @Anonymous
    @ApiOperation("查询待补全的地址")
    @PostMapping("list")
    public AjaxResult list(@Validated @RequestBody OpenForm openForm) {

        Set<String> jyList = openBizService.list(openForm.getPlatform());

        return AjaxResult.success(jyList);
    }


    @Anonymous
    @ApiOperation("补全地址")
    @PostMapping("/address")
    public AjaxResult save(@RequestBody AddressCompletedParams params) {

        orderAddressBizService.parse(params);

        return AjaxResult.success();
    }


    @Anonymous
    @ApiOperation("查询拼多多店铺待补全地址")
    @PostMapping("/pdd/address")
    public AjaxResult pddAddress(@RequestParam("shopName") String shopName) {

        List<PddShopVO> pddShopVOS = openBizService.pddAddress(shopName);

        return AjaxResult.success(pddShopVOS);
    }


}
