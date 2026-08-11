package com.ruoyi.subsidy.model.param;

import lombok.Data;
import lombok.experimental.Accessors;

/** 国补退款申请参数。 */
@Data
@Accessors(chain = true)
public class GbRefundApplyParam {
    private Long memberId;
    private String orderNo;
    private String reason;
}
