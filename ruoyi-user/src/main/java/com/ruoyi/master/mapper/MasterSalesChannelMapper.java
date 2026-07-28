package com.ruoyi.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.master.domain.MasterSalesChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 销售渠道数据访问接口。
 */
@Mapper
public interface MasterSalesChannelMapper extends BaseMapper<MasterSalesChannel> {

    /**
     * 按吉客云渠道 ID 幂等写入销售渠道。
     *
     * @param channel 销售渠道
     * @return 受影响行数
     */
    int upsertByJkyChannelId(MasterSalesChannel channel);
}
