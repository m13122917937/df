package com.ruoyi.order.convert;

import com.ruoyi.order.domain.JkyLogisticsTask;
import com.ruoyi.order.model.bo.JkyLogisticsTaskBO;
import com.ruoyi.order.model.param.JkyLogisticsTaskParam;
import com.ruoyi.order.model.query.JkyLogisticsTaskQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JkyLogisticsTaskCov {

    JkyLogisticsTaskCov INSTANCE = Mappers.getMapper(JkyLogisticsTaskCov.class);

    JkyLogisticsTaskBO toBO(JkyLogisticsTask task);

    List<JkyLogisticsTaskBO> listToBO(List<JkyLogisticsTask> list);

    JkyLogisticsTask paramToDomain(JkyLogisticsTaskParam param);
}
