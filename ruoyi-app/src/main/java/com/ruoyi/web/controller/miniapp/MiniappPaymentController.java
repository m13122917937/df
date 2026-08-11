package com.ruoyi.web.controller.miniapp;

import cn.hutool.core.lang.Assert;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.ruoyi.biz.miniapp.MiniappPaymentBizService;
import com.ruoyi.biz.miniapp.MiniappOrderBizService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.web.convert.miniapp.MiniappOrderWebConvert;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序支付接口。
 */
@RestController
@RequestMapping("/miniapp/payments")
public class MiniappPaymentController {
    private final MiniappPaymentBizService paymentBizService;
    private final MiniappOrderBizService orderBizService;

    public MiniappPaymentController(final MiniappPaymentBizService paymentBizService,
                                    final MiniappOrderBizService orderBizService) {
        this.paymentBizService = paymentBizService;
        this.orderBizService = orderBizService;
    }

    /**
     * 创建微信 JSAPI 预支付参数。
     *
     * @param orderNo 订单号
     * @return 小程序支付参数
     */
    @PostMapping("/{orderNo}/prepay")
    public AjaxResult prepay(@PathVariable final String orderNo) {
        return AjaxResult.success(paymentBizService.prepay(SecurityUtils.getUserId(), orderNo));
    }

    /** 查询当前会员订单的支付状态。 */
    @GetMapping("/{orderNo}/status")
    public AjaxResult status(@PathVariable final String orderNo) {
        Long memberId = SecurityUtils.getUserId();
        Assert.notNull(memberId, "登录已失效");
        return AjaxResult.success(MiniappOrderWebConvert.INSTANCE.toVO(orderBizService.get(memberId, orderNo)));
    }

    /**
     * 接收微信支付平台签名后的支付成功通知。
     *
     * @param payload 回调原文
     * @param request HTTP 请求
     * @return 微信平台应答
     */
    @Anonymous
    @PostMapping("/wechat/notify")
    public ResponseEntity<Map<String, String>> wechatNotify(@RequestBody final String payload,
                                                             final HttpServletRequest request) {
        paymentBizService.handleWechatNotify(payload, buildSignatureHeader(request));
        Map<String, String> response = new HashMap<>(2);
        response.put("code", "SUCCESS");
        response.put("message", "成功");
        return ResponseEntity.ok(response);
    }

    /** 接收微信退款平台签名后的退款通知。 */
    @Anonymous
    @PostMapping("/wechat/refund-notify")
    public ResponseEntity<Map<String, String>> wechatRefundNotify(@RequestBody final String payload,
                                                                   final HttpServletRequest request) {
        paymentBizService.handleWechatRefundNotify(payload, buildSignatureHeader(request));
        Map<String, String> response = new HashMap<>(2);
        response.put("code", "SUCCESS");
        response.put("message", "成功");
        return ResponseEntity.ok(response);
    }

    private SignatureHeader buildSignatureHeader(final HttpServletRequest request) {
        SignatureHeader header = new SignatureHeader();
        header.setNonce(request.getHeader("Wechatpay-Nonce"));
        header.setTimeStamp(request.getHeader("Wechatpay-Timestamp"));
        header.setSerial(request.getHeader("Wechatpay-Serial"));
        header.setSignature(request.getHeader("Wechatpay-Signature"));
        return header;
    }
}
