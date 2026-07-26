package com.ruoyi.web.convert.master;

import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import com.ruoyi.master.model.param.MasterSalesChannelDepositParam;
import com.ruoyi.web.vo.master.MasterSalesChannelDepositRequest;
import com.ruoyi.master.model.query.MasterSalesChannelQuery;
import com.ruoyi.web.vo.master.MasterSalesChannelQueryRequest;
import com.ruoyi.web.vo.master.MasterSalesChannelVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 销售渠道 Web 层对象转换器。
 */
@Mapper
public interface MasterSalesChannelWebConvert {

    MasterSalesChannelWebConvert INSTANCE = Mappers.getMapper(MasterSalesChannelWebConvert.class);

    /**
     * 转换销售渠道查询请求。
     *
     * @param source Web 查询请求
     * @return 领域查询条件
     */
    MasterSalesChannelQuery toQuery(MasterSalesChannelQueryRequest source);

    /**
     * 转换销售渠道保证金维护请求。
     *
     * @param source Web 保证金维护请求
     * @return 领域保证金维护参数
     */
    MasterSalesChannelDepositParam toDepositParam(MasterSalesChannelDepositRequest source);

    /**
     * 批量转换销售渠道响应。
     *
     * @param source 销售渠道业务对象集合
     * @return Web 响应集合
     */
    List<MasterSalesChannelVO> toVOList(List<MasterSalesChannelBO> source);
}
