package com.ruoyi.capital.convert;

import com.ruoyi.capital.domain.Recharge;
import com.ruoyi.capital.model.bo.RechargeBO;
import com.ruoyi.capital.model.param.RechargeParam;
import com.ruoyi.capital.model.query.RechargeQuery;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RechargeCov {

    RechargeCov INSTANCE = Mappers.getMapper(RechargeCov.class);

    Recharge toDomain(RechargeQuery rechargeQuery);

    RechargeBO toBO(Recharge one);

    Recharge paramToDomain(RechargeParam rechargeParam);

}
