package com.ruoyi.master.convert;

import com.ruoyi.jky.rep.sales.SalesChannelRep;
import com.ruoyi.master.domain.MasterSalesChannel;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 销售渠道领域对象转换器。
 */
@Mapper
public interface MasterSalesChannelConvert {

    MasterSalesChannelConvert INSTANCE = Mappers.getMapper(MasterSalesChannelConvert.class);

    /**
     * 将吉客云销售渠道信息转换为领域实体。
     *
     * @param source 吉客云销售渠道信息
     * @return 销售渠道实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jkyChannelId", source = "channelId")
    @Mapping(target = "platformCode", source = "onlinePlatTypeCode")
    @Mapping(target = "platformName", source = "onlinePlatTypeName")
    @Mapping(target = "channelDepartmentId", source = "channelDepartId")
    @Mapping(target = "channelDepartmentName", source = "channelDepartName")
    @Mapping(target = "jkySubjectId", source = "companyId")
    @Mapping(target = "subjectName", source = "companyName")
    @Mapping(target = "contactName", source = "linkMan")
    @Mapping(target = "contactPhone", source = "linkTel")
    @Mapping(target = "address", source = "officeAddress")
    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    MasterSalesChannel toDomain(SalesChannelRep source);

    /**
     * 将销售渠道实体转换为业务对象。
     *
     * @param domain 销售渠道实体
     * @return 销售渠道业务对象
     */
    MasterSalesChannelBO toBO(MasterSalesChannel domain);

    /**
     * 批量转换销售渠道业务对象。
     *
     * @param domains 销售渠道实体集合
     * @return 销售渠道业务对象集合
     */
    List<MasterSalesChannelBO> toBOList(List<MasterSalesChannel> domains);
}
