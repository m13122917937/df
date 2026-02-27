package com.ruoyi.order.convert;

import com.ruoyi.order.domain.PddOrderIncrement;
import com.ruoyi.order.model.bo.PddOrderIncrementBO;
import com.ruoyi.order.model.param.PddOrderIncrementParam;
import com.ruoyi.order.model.query.PddOrderIncrementQuery;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 拼多多增量订单对象转换接口
 *
 * @author ruoyi
 * @date 2025-02-08
 */
@Mapper(componentModel = "spring")
public interface PddOrderIncrementCov {

    PddOrderIncrementCov INSTANCE = org.mapstruct.factory.Mappers.getMapper(PddOrderIncrementCov.class);

    /**
     * Domain转BO
     */
    PddOrderIncrementBO toBO(PddOrderIncrement domain);

    /**
     * BO列表转Domain列表
     */
    List<PddOrderIncrementBO> listToBO(List<PddOrderIncrement> domains);

    /**
     * Param转Domain
     */
    PddOrderIncrement paramToDomain(PddOrderIncrementParam param);

    /**
     * Query转Domain
     */
    PddOrderIncrement queryToDomain(PddOrderIncrementQuery query);

    /**
     * 更新Domain
     */
    void updateDomain(PddOrderIncrementParam param, @MappingTarget PddOrderIncrement domain);
}