package com.ruoyi.biz.miniapp;

import com.ruoyi.subsidy.facade.IGbRefundFacade;
import com.ruoyi.subsidy.model.bo.GbRefundBO;
import com.ruoyi.subsidy.model.param.GbRefundApplyParam;
import com.ruoyi.web.form.miniapp.MiniappRefundApplyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 小程序退款应用编排。 */
@Component
@RequiredArgsConstructor
public class MiniappRefundBizService {
    private final IGbRefundFacade refundFacade;

    /** 提交当前会员退款申请。 */
    public GbRefundBO apply(final Long memberId, final String orderNo, final MiniappRefundApplyRequest request) {
        return refundFacade.apply(new GbRefundApplyParam().setMemberId(memberId).setOrderNo(orderNo).setReason(request.getReason()));
    }
}
