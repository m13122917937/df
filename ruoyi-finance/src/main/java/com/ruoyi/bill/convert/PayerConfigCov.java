package com.ruoyi.bill.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.PayerConfig;
import com.ruoyi.bill.model.bo.PayerConfigBO;
import com.ruoyi.bill.model.query.PayerConfigQuery;
import com.ruoyi.bill.model.param.PayerConfigParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PayerConfigCov {

    PayerConfigCov INSTANCE = Mappers.getMapper(PayerConfigCov.class);


    List<PayerConfigBO>  listToBO(List<PayerConfig>  list );

    PayerConfigBO   toBO(PayerConfig  list );

    PayerConfig  queryToDomain(PayerConfigQuery query);

    PayerConfig  paramToDomain(PayerConfigParam param);
}

