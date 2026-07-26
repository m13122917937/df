package com.ruoyi.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.master.domain.MasterSalesChannel;
import com.ruoyi.master.mapper.MasterSalesChannelMapper;
import com.ruoyi.master.model.param.MasterSalesChannelDepositParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 销售渠道主数据服务。
 */
@Service
@RequiredArgsConstructor
public class MasterSalesChannelService extends ServiceImpl<MasterSalesChannelMapper, MasterSalesChannel> {

    /**
     * 幂等写入单页销售渠道数据。
     *
     * @param channels 销售渠道集合
     */
    @Transactional(rollbackFor = Exception.class)
    public void upsertPage(final List<MasterSalesChannel> channels) {
        for (MasterSalesChannel channel : channels) {
            baseMapper.upsertByJkyChannelId(channel);
        }
    }

    /**
     * 更新销售渠道保证金。
     *
     * @param param 保证金维护参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeposit(final MasterSalesChannelDepositParam param) {
        int affectedRows = baseMapper.updateDeposit(param.getId(), param.getDepositAmount());
        if (affectedRows == 0) {
            throw new ServiceException("销售渠道不存在");
        }
    }
}
