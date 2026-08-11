package com.ruoyi.subsidy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.subsidy.domain.GbRefund;
import com.ruoyi.subsidy.mapper.GbRefundMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/** 国补退款领域服务。 */
@Service
public class GbRefundService extends ServiceImpl<GbRefundMapper, GbRefund> {
    /** 按退款单号查询退款单。 */
    public GbRefund getByRefundNo(final String refundNo) {
        return baseMapper.selectByRefundNo(refundNo);
    }
    /** 查询后台退款列表。 */
    public List<GbRefund> listForAdmin(final String refundStatus) {
        return baseMapper.selectAdminList(refundStatus);
    }
}
