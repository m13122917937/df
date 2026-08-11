package com.ruoyi.biz.miniapp;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.config.properties.WxMiniappProperties;
import com.ruoyi.config.properties.WxPayProperties;
import com.ruoyi.subsidy.facade.IGbOrderFacade;
import com.ruoyi.subsidy.facade.IGbRefundFacade;
import com.ruoyi.subsidy.model.bo.GbRefundPaymentBO;
import com.ruoyi.subsidy.model.bo.GbPaymentOrderBO;
import com.ruoyi.user.facade.IWechatIdentityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 小程序微信支付应用编排。
 */
@Component
@RequiredArgsConstructor
public class MiniappPaymentBizService {
    private final IGbOrderFacade orderFacade;
    private final IWechatIdentityFacade identityFacade;
    private final WxMiniappProperties miniappProperties;
    private final WxPayProperties payProperties;
    private final WxPayService wxPayService;
    private final IGbRefundFacade refundFacade;

    /**
     * 创建 JSAPI 预支付订单。
     *
     * @param memberId 当前会员
     * @param orderNo 订单号
     * @return 微信支付参数
     */
    public Object prepay(final Long memberId, final String orderNo) {
        Assert.isTrue(identityFacade.isPurchaseAllowed(memberId, "MINIAPP", miniappProperties.getAppId()),
                "微信身份尚未完成归并，暂不支持支付");
        GbPaymentOrderBO order = orderFacade.getPendingPayment(memberId, orderNo);
        String openId = identityFacade.getOpenId(memberId, "MINIAPP", miniappProperties.getAppId());
        Assert.notBlank(openId, "微信身份未完成归并");
        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        request.setAppid(miniappProperties.getAppId());
        request.setMchid(payProperties.getMchId());
        request.setDescription("国补专区商品订单");
        request.setOutTradeNo(order.getPaymentNo());
        request.setNotifyUrl(payProperties.getNotifyBaseUrl() + "/miniapp/payments/wechat/notify");
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setCurrency("CNY");
        amount.setTotal(NumberUtil.mul(order.getPayAmount(), 100).intValue());
        request.setAmount(amount);
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(openId);
        request.setPayer(payer);
        try {
            return wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);
        } catch (WxPayException exception) {
            throw new ServiceException("微信预支付创建失败");
        }
    }

    /**
     * 验证并处理微信支付通知。
     *
     * @param payload 微信回调原文
     * @param signatureHeader 微信签名头
     */
    public void handleWechatNotify(final String payload, final SignatureHeader signatureHeader) {
        try {
            WxPayNotifyV3Result notifyResult = wxPayService.parseOrderNotifyV3Result(payload, signatureHeader);
            BigDecimal paidAmount = BigDecimal.valueOf(notifyResult.getResult().getAmount().getTotal(), 2);
            orderFacade.markWechatPaid(notifyResult.getResult().getOutTradeNo(),
                    notifyResult.getResult().getTransactionId(), paidAmount);
        } catch (WxPayException exception) {
            throw new ServiceException("微信支付回调验签失败");
        }
    }

    /** 发起已审核退款单的微信原路退款。 */
    public void refund(final String refundNo) {
        GbRefundPaymentBO refund = refundFacade.approve(refundNo);
        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setTransactionId(refund.getWechatTransactionId());
        request.setOutRefundNo(refund.getRefundNo());
        request.setReason(refund.getReason());
        request.setNotifyUrl(payProperties.getNotifyBaseUrl() + "/miniapp/payments/wechat/refund-notify");
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setRefund(NumberUtil.mul(refund.getAmount(), 100).intValue());
        amount.setTotal(NumberUtil.mul(refund.getAmount(), 100).intValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);
        try {
            WxPayRefundV3Result result = wxPayService.refundV3(request);
            if ("SUCCESS".equals(result.getStatus())) {
                refundFacade.completeWechatRefund(refundNo, result.getRefundId());
            }
        } catch (WxPayException exception) {
            refundFacade.markWechatRefundFailed(refundNo);
            throw new ServiceException("微信退款发起失败");
        }
    }

    /** 验签并处理微信退款成功通知。 */
    public void handleWechatRefundNotify(final String payload, final SignatureHeader signatureHeader) {
        try {
            WxPayRefundNotifyV3Result notifyResult = wxPayService.parseRefundNotifyV3Result(payload, signatureHeader);
            if ("SUCCESS".equals(notifyResult.getResult().getRefundStatus())) {
                refundFacade.completeWechatRefund(notifyResult.getResult().getOutRefundNo(),
                        notifyResult.getResult().getRefundId());
            }
        } catch (WxPayException exception) {
            throw new ServiceException("微信退款回调验签失败");
        }
    }
}
