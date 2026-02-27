package com.ruoyi.biz.pay;


import cn.hutool.core.date.DateUtil;
import com.ruoyi.biz.pay.handler.PayHandler;
import com.ruoyi.biz.pay.model.PayNotifyParam;
import com.ruoyi.biz.pay.model.PayResult;
import com.ruoyi.biz.pay.model.PrePayParam;
import com.ruoyi.biz.pay.model.PrePayResult;
import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.facade.IPaymentFacade;
import com.ruoyi.capital.model.bo.PaymentBO;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.consts.PaymentConsts;
import com.ruoyi.capital.model.param.CompanyCapitalLogParam;
import com.ruoyi.capital.model.param.PaymentParam;
import com.ruoyi.capital.model.query.PaymentQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.user.facade.ICompanyFacade;
import com.ruoyi.user.model.bo.CompanyBO;
import com.ruoyi.user.model.query.CompanyQuery;
import com.ruoyi.web.form.pay.PayForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.TimerTask;

/**
 * 第三方支付相关服务.
 */
@Slf4j
@Service
public class ThirdPayService {

    @Autowired
    private PaymentBizService paymentBizService;

    @Resource
    private ICompanyFacade companyFacade;

    @Autowired
    private ICompanyCapitalFacade iCompanyCapitalFacade;

    @Resource
    private IPaymentFacade paymentFacade;

    public PrePayResult prePay(final Long userId, final Long companyId, final PayTypes payType, final PayForm payForm) {
        // 基础校验
        verify(companyId, payForm);

        PayHandler payHandler = SpringUtils.getBean(payType.getHandler());
        PrePayParam prePayParam = payHandler.buildPrePayParam(userId, payType, payForm);
        // 保存订单记录
        paymentBizService.savePayment(userId, companyId, payType, prePayParam.getTradeNo(), payForm);

        PrePayResult result = payHandler.doPrePay(prePayParam);
        result.setTradeNo(prePayParam.getTradeNo());
        return result;
    }

    private void verify(Long companyId, PayForm payForm) {
        CompanyBO companyBO = companyFacade.queryOne(new CompanyQuery().setId(companyId));
        if (Objects.isNull(companyBO)) {
            throw new ServiceException("登录异常，请重新登录");
        }
//        if (!Objects.equals(CompanyParam.END_AUTH, companyBO.getAuth())) {
//            throw new ServiceException("请先完成认证之后再充值保证金");
//        }
        // 企业成立时间必须满足半年
        if (Objects.nonNull(companyBO.getEstablishedTime()) && companyBO.getEstablishedTime().after(DateUtil.offsetMonth(new Date(), -6))) {
            if (Arith.gt(payForm.getAmount(), new BigDecimal(10000))) {
                throw new ServiceException("系统检测到您的企业成立未满半年，至少需充值10000元");
            }
        }
    }

    public boolean payStatus(String tradeNo) {
        PaymentBO payment = paymentFacade.queryOne(new PaymentQuery().setTradeNo(tradeNo));
        //非预支付状态，说明已经处理过了
        if (!PaymentConsts.PAY_PRE.equals(payment.getTradeStates())) {
            return PaymentConsts.PAY_SUCCESS.equals(payment.getTradeStates());
        }
        PayTypes payType = PayTypes.fromCode(payment.getTradeChannel());
        PayHandler payHandler = SpringUtils.getBean(payType.getHandler());
        PayResult payResult = payHandler.payStatus(tradeNo);
        return payResult.getTradeState();
    }

    public String payNotify(final PayTypes payType, final String tradeNo, final String notifyData) {
        PayHandler payHandler = SpringUtils.getBean(payType.getHandler());
        PayNotifyParam param = new PayNotifyParam();
        param.setNotifyData(notifyData);
        PayResult notifyResult = payHandler.doPayNotify(param);

        //校验支付数据
        PaymentBO payment = paymentFacade.queryOne(new PaymentQuery().setTradeNo(tradeNo));
        //防止使用A渠道支付，然后模拟B渠道的回调
        if (payType.getCode() != payment.getTradeChannel()) {
            throw new ServiceException("支付回调数据异常");
        }
        // 非模拟支付，校验支付金额
        if (payType != PayTypes.SIMULATE && payment.getAmount().compareTo(notifyResult.getAmount()) != 0) {
            log.warn("回调接口支付金额不对：{} ->[{} != {}]", tradeNo, payment.getAmount(), notifyResult.getAmount());
            throw new ServiceException("支付金额错误");
        }
        // 修改充值记录
        PaymentParam paymentParam = new PaymentParam().setTradeStates(notifyResult.getTradeState() ? PaymentConsts.PAY_SUCCESS : PaymentConsts.PAY_FAIL)
            .setOutNo(notifyResult.getThirdTradeNo()).setNotifyTime(DateUtil.date());
        boolean update = paymentFacade.update(paymentParam, new PaymentQuery().setId(payment.getId()));
        if (update) {
            // 添加保证金
            CompanyCapitalLogParam capitalDetailParam = new CompanyCapitalLogParam().setOutAmount(BigDecimal.ZERO)
                .setOrderNo(payment.getTradeNo()).setCompanyId(payment.getCompanyId()).setAddAmount(payment.getAmount())
                .setRemark("充值").setType(CompanyCapitalConsts.LogTypes.CHARGE.getCode());
            iCompanyCapitalFacade.changeAvailable(capitalDetailParam);
        }

        return notifyResult.getThirdData();
    }

}
