package com.ruoyi.biz.pay.handler;

import com.ruoyi.biz.pay.model.PrePayParam;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.consts.TradeTypes;
import com.ruoyi.web.form.pay.PayForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 抽象支付处理.
 *
 */
public abstract class AbstractPayHandler implements PayHandler {

    protected String PAY_SUCCESS = "支付成功";

    @Autowired
    RuoYiConfig ruoYiConfig;

    @Override
    public PrePayParam buildPrePayParam(final Long userId, final PayTypes payType, final PayForm payForm) {
        PrePayParam prePayParam = new PrePayParam();

        TradeTypes tradeType = TradeTypes.fromCode(payForm.getTradeType());
        String tradeNo = WebConstants.snowflake.nextIdStr();

        prePayParam.setPayType(payType).setAmount(payForm.getAmount())
                .setTradeType(tradeType).setTradeNo(tradeNo)
                .setNotifyUrl(String.format(WebConstants.PAY_NOTIFY_URL, ruoYiConfig.getHost(), payType.getCode(), prePayParam.getTradeNo()));
        return prePayParam;
    }

    protected HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? ((ServletRequestAttributes) requestAttributes).getRequest() : null;
    }
}
