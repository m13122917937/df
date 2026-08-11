package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.ruoyi.biz.miniapp.MiniappAddressBizService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.form.miniapp.MiniappAddressRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 小程序收货地址接口。 */
@RestController
@RequestMapping("/miniapp/addresses")
public class MiniappAddressController {
    private final MiniappAddressBizService addressBizService;

    public MiniappAddressController(final MiniappAddressBizService addressBizService) {
        this.addressBizService = addressBizService;
    }

    /** 查询当前会员的地址列表。 */
    @GetMapping
    public AjaxResult list() {
        return AjaxResult.success(addressBizService.list(currentMemberId()));
    }

    /** 新增或修改当前会员地址。 */
    @PostMapping
    public AjaxResult save(@RequestBody final MiniappAddressRequest request) {
        return AjaxResult.success(addressBizService.save(currentMemberId(), request));
    }

    /** 修改当前会员地址。 */
    @PutMapping("/{addressId}")
    public AjaxResult update(@PathVariable final Long addressId, @RequestBody final MiniappAddressRequest request) {
        request.setId(addressId);
        return AjaxResult.success(addressBizService.save(currentMemberId(), request));
    }

    /** 删除当前会员地址。 */
    @DeleteMapping("/{addressId}")
    public AjaxResult remove(@PathVariable final Long addressId) {
        addressBizService.remove(currentMemberId(), addressId);
        return AjaxResult.success();
    }

    private Long currentMemberId() {
        Long memberId = SecurityUtils.getUserId();
        Assert.notNull(memberId, "登录已失效");
        return memberId;
    }
}
