package com.ruoyi.master.facade;

import com.ruoyi.common.model.PageParamV2;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.master.model.param.MasterSalesChannelDepositParam;
import com.ruoyi.master.model.query.MasterSalesChannelQuery;

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
     * 更新销售渠道保证金。
     *
     * @param param 保证金维护参数
     */
    void updateDeposit(MasterSalesChannelDepositParam param);

    /**
     * 同步吉客云销售渠道数据。
     */
    void syncSalesChannels();
}
