package com.ruoyi.biz.pay.handler;


import com.ruoyi.biz.pay.model.PayNotifyParam;
import com.ruoyi.biz.pay.model.PayResult;
import com.ruoyi.biz.pay.model.PrePayParam;
import com.ruoyi.biz.pay.model.PrePayResult;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.web.form.pay.PayForm;

/**
 * 支付处理.
 *
 * @date 2022/5/30
 */
public interface PayHandler {

    /**
     * 构建预支付参数.
     *
     * @param userId  用户id
     * @param payType 支付类型
     * @param payForm 支付参数
     * @return 预支付参数
     */
    PrePayParam buildPrePayParam(Long userId, PayTypes payType, PayForm payForm);

    /**
     * 各渠道下单预支付
     *
     * @param param 预支付参数
     * @return 预支付结果
     */
    PrePayResult doPrePay(PrePayParam param);

    /**
     * 查询支付单支付结果
     *
     * @param tradeNo 支付单号
     * @return 支付结果
     */
    PayResult payStatus(String tradeNo);

    /**
     * 支付结果回调数据解析处理
     *
     * @param param 支付单回调数据
     * @return 支付结果
     */
    PayResult doPayNotify(PayNotifyParam param);

}
