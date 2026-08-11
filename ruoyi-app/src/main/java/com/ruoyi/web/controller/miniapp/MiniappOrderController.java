package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.miniapp.MiniappOrderBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.subsidy.model.bo.GbOrderBO;
import com.ruoyi.web.convert.miniapp.MiniappOrderWebConvert;
import com.ruoyi.web.form.miniapp.MiniappOrderCreateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序订单接口。
 */
@RestController
@RequestMapping("/miniapp/orders")
public class MiniappOrderController {
    private final MiniappOrderBizService orderBizService;

    public MiniappOrderController(final MiniappOrderBizService orderBizService) {
        this.orderBizService = orderBizService;
    }

    /**
     * 创建待支付订单。
     *
     * @param request 下单请求
     * @return 订单信息
     */
    @PostMapping
    public AjaxResult create(@RequestBody final MiniappOrderCreateRequest request) {
        Assert.notNull(SecurityUtils.getUserId(), "登录已失效");
        GbOrderBO order = orderBizService.create(SecurityUtils.getUserId(), request);
        return AjaxResult.success(MiniappOrderWebConvert.INSTANCE.toVO(order));
    }

    /** 查询当前会员订单列表。 */
    @GetMapping
    public AjaxResult list() {
        Assert.notNull(SecurityUtils.getUserId(), "登录已失效");
        return AjaxResult.success(MiniappOrderWebConvert.INSTANCE.toVOList(orderBizService.list(SecurityUtils.getUserId())));
    }

    /** 查询当前会员所属订单详情。 */
    @GetMapping("/{orderNo}")
    public AjaxResult get(@PathVariable final String orderNo) {
        Long memberId = SecurityUtils.getUserId();
        Assert.notNull(memberId, "登录已失效");
        return AjaxResult.success(MiniappOrderWebConvert.INSTANCE.toVO(orderBizService.get(memberId, orderNo)));
    }

    /** 当前会员确认订单收货。 */
    @PostMapping("/{orderNo}/confirm-received")
    public AjaxResult confirmReceived(@PathVariable final String orderNo) {
        Assert.notNull(SecurityUtils.getUserId(), "登录已失效");
        orderBizService.confirmReceived(SecurityUtils.getUserId(), orderNo);
        return AjaxResult.success();
    }

    /** 当前会员取消待支付订单。 */
    @PostMapping("/{orderNo}/cancel")
    public AjaxResult cancel(@PathVariable final String orderNo) {
        Assert.notNull(SecurityUtils.getUserId(), "登录已失效");
        orderBizService.cancel(SecurityUtils.getUserId(), orderNo);
        return AjaxResult.success();
    }
}
