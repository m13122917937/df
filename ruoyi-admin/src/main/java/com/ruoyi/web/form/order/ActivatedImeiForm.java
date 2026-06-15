package com.ruoyi.web.form.order;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

/**
 * 串码激活状态人工放行表单
 */
@Data
public class ActivatedImeiForm {

    @NotBlank(message = "订单号不能为空")
    private String orderCode;

    private String sn;

    private String imei;

    /**
     * sn 与 imei 至少有一项非空
     */
    @AssertTrue(message = "sn 与 imei 至少需要填写一项")
    public boolean isSnOrImeiPresent() {
        return StrUtil.isNotBlank(sn) || StrUtil.isNotBlank(imei);
    }
}
