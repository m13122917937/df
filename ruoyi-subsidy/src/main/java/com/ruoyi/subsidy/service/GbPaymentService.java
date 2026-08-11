package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbPayment;
import com.ruoyi.subsidy.mapper.GbPaymentMapper;
import org.springframework.stereotype.Service;

/**
 * 国补支付单领域服务。
 */
@Service
public class GbPaymentService extends ServiceImpl<GbPaymentMapper, GbPayment> {

    /**
     * 查询订单支付单。
     *
     * @param orderId 订单ID
     * @return 支付单
     */
    public GbPayment getByOrderId(final Long orderId) {
        return baseMapper.selectByOrderId(orderId);
    }

    /**
     * 查询支付单号对应的支付单。
     *
     * @param paymentNo 支付单号
     * @return 支付单
     */
    public GbPayment getByPaymentNo(final String paymentNo) {
        return baseMapper.selectByPaymentNo(paymentNo);
    }
}
