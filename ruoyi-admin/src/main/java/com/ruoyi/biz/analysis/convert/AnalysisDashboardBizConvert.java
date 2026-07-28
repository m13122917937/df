package com.ruoyi.biz.analysis.convert;

import com.ruoyi.analysis.model.bo.AnalysisStoreOptionBO;
import com.ruoyi.master.model.bo.MasterSalesChannelBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 经营统计应用编排对象转换器。
 */
@Mapper
public interface AnalysisDashboardBizConvert {

    /** 转换器实例。 */
    AnalysisDashboardBizConvert INSTANCE = Mappers.getMapper(AnalysisDashboardBizConvert.class);

    /**
     * 将销售渠道主数据转换为经营统计店铺选项。
     *
     * @param source 销售渠道主数据
     * @return 店铺选项
     */
    @Mapping(target = "channelId", source = "id")
    @Mapping(target = "shopName", source = "channelName")
    AnalysisStoreOptionBO toStoreOption(MasterSalesChannelBO source);

    /**
     * 批量转换销售渠道主数据。
     *
     * @param source 销售渠道主数据集合
     * @return 店铺选项集合
     */
    List<AnalysisStoreOptionBO> toStoreOptionList(List<MasterSalesChannelBO> source);
}
