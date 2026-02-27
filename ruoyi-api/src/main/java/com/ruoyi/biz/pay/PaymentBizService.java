package com.ruoyi.biz.pay;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.capital.facade.IPaymentFacade;
import com.ruoyi.capital.model.bo.PaymentBO;
import com.ruoyi.capital.model.consts.PaymentConsts;
import com.ruoyi.capital.model.param.PaymentParam;
import com.ruoyi.capital.model.query.PaymentQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.consts.PayTypes;
import com.ruoyi.web.form.pay.PayForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 系统支付需要处理的业务.
 *
 * @date 2023/2/12
 */
@Slf4j
@Service
public class PaymentBizService {

    @Resource
    private IPaymentFacade paymentFacade;


    /**
     * 保存预支付记录
     *
     * @param userId    用户id
     * @param companyId 企业id
     * @param payType   支付类型
     * @param tradeNo   支付单号
     * @param payForm   支付数据
     */
    public void savePayment(final Long userId, final Long companyId, final PayTypes payType, final String tradeNo, final PayForm payForm) {
        // 保存订单记录
        PaymentParam paymentParam = new PaymentParam();
        paymentParam.setCompanyId(companyId)
            .setCreateBy(userId).setAmount(payForm.getAmount()).setTradeNo(tradeNo)
            .setTradeChannel(payType.getCode()).setTradeStates(PaymentConsts.PAY_PRE)
            .setTradeType(payForm.getTradeType()).setCreateTime(DateUtil.date())
                ;

        paymentFacade.save(paymentParam);
    }


    /**
     * 查询支付记录
     *
     * @param tradeNo 支付单号
     * @return 支付记录数据
     */
    public PaymentBO findPayment(final String tradeNo) {
        PaymentBO payment = paymentFacade.queryOne(new PaymentQuery().setTradeNo(tradeNo));
        if (Objects.isNull(payment)) {
            log.warn("当前支付记录【{}】不存在", tradeNo);
            throw new ServiceException("支付记录不存在");
        }
        return payment;
    }

}
