package com.ruoyi.subsidy.facade;

import com.ruoyi.subsidy.model.bo.GbRefundBO;
import com.ruoyi.subsidy.model.param.GbRefundApplyParam;

/** 国补退款领域出口。 */
public interface IGbRefundFacade {
    /** 提交未发货订单的整单退款申请。 */
    GbRefundBO apply(GbRefundApplyParam param);
    /** 审核退款并获取微信退款参数。 */
    com.ruoyi.subsidy.model.bo.GbRefundPaymentBO approve(String refundNo);
    /** 微信退款调用失败时回退状态。 */
    void markWechatRefundFailed(String refundNo);
    /** 处理微信退款成功通知并恢复库存。 */
    boolean completeWechatRefund(String refundNo, String wechatRefundId);
    /** 查询后台退款列表。 */
    java.util.List<com.ruoyi.subsidy.domain.GbRefund> listForAdmin(String refundStatus);
}
