package com.ruoyi.biz.subsidy;

import cn.hutool.core.util.NumberUtil;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.config.properties.AdminWxPayProperties;
import com.ruoyi.subsidy.facade.IGbRefundFacade;
import com.ruoyi.subsidy.model.bo.GbRefundPaymentBO;
import com.ruoyi.subsidy.domain.GbRefund;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/** 国补后台退款审核应用编排。 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wechat.pay", name = "mch-id")
public class SubsidyRefundBizService {
    private final IGbRefundFacade refundFacade;
    private final WxPayService adminWxPayService;
    private final AdminWxPayProperties payProperties;

    /** 审核通过并在事务外发起微信原路退款。 */
    public void approve(final String refundNo) {
        GbRefundPaymentBO refund = refundFacade.approve(refundNo);
        try {
            WxPayRefundV3Result result = adminWxPayService.refundV3(buildRequest(refund));
            if ("SUCCESS".equals(result.getStatus())) {
                refundFacade.completeWechatRefund(refundNo, result.getRefundId());
            }
        } catch (WxPayException exception) {
            refundFacade.markWechatRefundFailed(refundNo);
            throw new ServiceException("微信退款发起失败");
        }
    }

    /** 查询退款列表。 */
    public List<GbRefund> list(final String refundStatus) {
        return refundFacade.listForAdmin(refundStatus);
    }

    private WxPayRefundV3Request buildRequest(final GbRefundPaymentBO refund) {
        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setTransactionId(refund.getWechatTransactionId());
        request.setOutRefundNo(refund.getRefundNo());
        request.setReason(refund.getReason());
        request.setNotifyUrl(payProperties.getNotifyBaseUrl() + "/miniapp/payments/wechat/refund-notify");
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setRefund(NumberUtil.mul(refund.getAmount(), 100).intValue());
        amount.setTotal(NumberUtil.mul(refund.getAmount(), 100).intValue());
        amount.setCurrency("CNY");
        return request.setAmount(amount);
    }
}
