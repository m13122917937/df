package com.ruoyi.master.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.master.model.query.MasterSalesChannelQuery;

import java.util.List;

/**
 * 销售渠道主数据领域对外接口。
 */
public interface IMasterSalesChannelFacade {

    /**
     * 分页查询销售渠道。
     *
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 销售渠道分页数据
     */
    PageBO<MasterSalesChannelBO> page(MasterSalesChannelQuery query, PageParamV2 pageParam);

    /**
     * 查询可用于经营分析配置的平台与店铺。
     *
     * @return 销售渠道主数据集合
     */
    List<MasterSalesChannelBO> listStoreOptions();

    /**
     * 同步吉客云销售渠道数据。
     */
    void syncSalesChannels();
}
