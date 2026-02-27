package com.ruoyi.biz.pay.handler;


import cn.hutool.core.util.NumberUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.ruoyi.biz.pay.model.PayNotifyParam;
import com.ruoyi.biz.pay.model.PayResult;
import com.ruoyi.biz.pay.model.PrePayParam;
import com.ruoyi.biz.pay.model.PrePayResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.config.properties.WxMpProperties;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.consts.TradeTypes;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.user.facade.IUserFacade;
import com.ruoyi.user.model.bo.UserBO;
import com.ruoyi.user.model.query.UserQuery;
import com.ruoyi.web.form.pay.PayForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 微信支付处理器.
 *
 * @date 2022/5/30
 */
@Slf4j
@Component
public class WxPayHandler extends AbstractPayHandler implements PayHandler {

    /**
     * 微信支付成功状态
     */
    private static final String PAY_SUCCESS = "SUCCESS";

    @Autowired
    private WxMpProperties wxMpProperties;
    @Autowired
    private WxPayService wxPayService;
    @Resource
    private IUserFacade userFacade;

    @Override
    public PrePayParam buildPrePayParam(final Long userId, final PayTypes payType, final PayForm payForm) {
        PrePayParam prePayParam = super.buildPrePayParam(userId, payType, payForm);
        PrePayParam.WxPayParam wxPayParam = new PrePayParam.WxPayParam();

        wxPayParam.setClientIp(IpUtils.getIpAddr(getRequest()));
//        boolean isMiniapp = prePayParam.getTradeType() == TradeTypes.MINIAPP;

        if (prePayParam.getTradeType() == TradeTypes.JSAPI || prePayParam.getTradeType() == TradeTypes.MINIAPP) {
            UserBO userBO = userFacade.queryOne(new UserQuery().setUserId(userId));
            if (Objects.isNull(userBO)) {
                throw new ServiceException("用户不存在");
            }
            wxPayParam.setOpenid(userBO.getOpenId());
        }
//        String appId = isMiniapp ? wxMaProperties.getConfigs().get(ApiConsts.MA_WC).getAppId() : wxMpProperties.getConfigs().get(0).getAppId();
        wxPayParam.setAppid(wxMpProperties.getConfigs().get(0).getAppId());
        prePayParam.setWxPayParam(wxPayParam);
        return prePayParam;
    }

    @Override
    public PrePayResult doPrePay(final PrePayParam param) {
        PrePayParam.WxPayParam wxPayParam = param.getWxPayParam();
        if (Objects.isNull(wxPayParam)) {
            throw new ServiceException("微信支付参数缺失");
        }
        TradeTypeEnum typeEnum = param.getTradeType().getTypeEnum();
        //构建支付请求参数
        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        request.setAppid(wxPayParam.getAppid());
        if (typeEnum == TradeTypeEnum.JSAPI) {
            WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
            payer.setOpenid(wxPayParam.getOpenid());
            request.setPayer(payer);
        }

        //body字段长度不能大于128
        request.setDescription("保证金充值");
        request.setOutTradeNo(param.getTradeNo());
        //微信支付金额单位为分
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setCurrency("CNY");
        amount.setTotal(NumberUtil.mul(param.getAmount(), 100).intValue());
        request.setAmount(amount);
        request.setNotifyUrl(param.getNotifyUrl());
        try {
            Object result = wxPayService.createOrderV3(typeEnum, request);
            PrePayResult prePayResult = new PrePayResult();
            prePayResult.setData(result);
            return prePayResult;
        } catch (WxPayException e) {
            log.error("微信预支付失败:[{}]:", param, e);
            throw new ServiceException("微信预支付失败");
        }
    }

    @Override
    public PayResult payStatus(final String tradeNo) {
        try {
            WxPayOrderQueryV3Result result = wxPayService.queryOrderV3(null, tradeNo);
            int total = NumberUtil.div(new BigDecimal(result.getAmount().getTotal()), 100).intValue();
            return this.buildPayResult(result.getTradeState(), result.getTransactionId(), new BigDecimal(total));
        } catch (WxPayException e) {
            log.error("微信查询支付状态错误:[{}]:", tradeNo, e);
        }
        return PayResult.failure();
    }

    @Override
    public PayResult doPayNotify(final PayNotifyParam param) {
        try {
            HttpServletRequest request = this.getRequest();
            if (Objects.isNull(request)) {
                throw new ServiceException("非法请求");
            }
            SignatureHeader signatureHeader = new SignatureHeader();
            signatureHeader.setNonce(request.getHeader(WebConstants.NONCE_HEADER));
            signatureHeader.setTimeStamp(request.getHeader(WebConstants.TIMESTAMP_HEADER));
            signatureHeader.setSerial(request.getHeader(WebConstants.SERIALNO_HEADER));
            signatureHeader.setSignature(request.getHeader(WebConstants.SINGED_HEADER));

            log.info("微信支付回调处理数据：{}", param);
            WxPayNotifyV3Result notifyV3Result = wxPayService.parseOrderNotifyV3Result(param.getNotifyData(), signatureHeader);

            WxPayNotifyV3Result.DecryptNotifyResult result = notifyV3Result.getResult();
            //返回给第三方数据

            int total = NumberUtil.div(new BigDecimal(result.getAmount().getTotal()), 100).intValue();

            PayResult notifyResult = this.buildPayResult(result.getTradeState(), result.getTransactionId(), new BigDecimal(total));
            notifyResult.setThirdData(PAY_SUCCESS);
            return notifyResult;
        } catch (Exception e) {
            log.error("微信支付通知处理失败：", e);
            throw new ServiceException("支付通知结果处理失败");
        }
    }

    private PayResult buildPayResult(final String tradeState, final String thirdTradeNo, final BigDecimal amount) {
        PayResult notifyResult = new PayResult();
        notifyResult.setTradeState(PAY_SUCCESS.equals(tradeState))
            .setThirdTradeNo(thirdTradeNo)
            .setAmount(amount);
        return notifyResult;
    }
}
