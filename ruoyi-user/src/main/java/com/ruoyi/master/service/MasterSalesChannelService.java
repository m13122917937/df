package com.ruoyi.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.master.domain.MasterSalesChannel;
import com.ruoyi.master.mapper.MasterSalesChannelMapper;
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

}
