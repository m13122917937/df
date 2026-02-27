package com.ruoyi.biz.pay.handler;

import cn.hutool.core.util.RandomUtil;
import com.ruoyi.biz.pay.model.PayNotifyParam;
import com.ruoyi.biz.pay.model.PayResult;
import com.ruoyi.biz.pay.model.PrePayParam;
import com.ruoyi.biz.pay.model.PrePayResult;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.consts.WebConstants;
import com.ruoyi.consts.TradeTypes;
import com.ruoyi.framework.manager.AsyncManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * 模拟支付处理器.
 *
 */
@Slf4j
@Component
public class SimulatePayHandler extends AbstractPayHandler implements PayHandler {

    @Override
    public PrePayResult doPrePay(final PrePayParam prePayParam) {
        String notifyUrl = prePayParam.getNotifyUrl();
        if (prePayParam.getTradeType() != TradeTypes.QRCODE) {
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(RandomUtil.randomLong(2000L));
                    } catch (Exception e) {
                    }
                    String payResult = HttpUtils.sendGet(notifyUrl);
                    log.info("模拟支付预支付请求[{}]:{}", notifyUrl, payResult);
                }
            });
        }
        return new PrePayResult().setData(prePayParam.getNotifyUrl());
    }

    @Override
    public PayResult payStatus(final String tradeNo) {
        return this.buildPayResult();
    }

    @Override
    public PayResult doPayNotify(final PayNotifyParam param) {
        return this.buildPayResult();
    }

    private PayResult buildPayResult() {
        PayResult notifyResult = new PayResult();
        //返回给第三方数据
        notifyResult.setTradeState(true)
            .setThirdTradeNo("MN" + WebConstants.snowflake.nextIdStr())
            .setThirdData(PAY_SUCCESS);
        return notifyResult;
    }
}
