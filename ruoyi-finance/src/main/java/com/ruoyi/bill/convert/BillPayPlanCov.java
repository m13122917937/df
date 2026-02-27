package com.ruoyi.bill.convert;


import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.BillPayPlan;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.query.BillPayPlanQuery;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BillPayPlanCov {

    BillPayPlanCov INSTANCE = Mappers.getMapper(BillPayPlanCov.class);


    List<BillPayPlanBO>  listToBO(List<BillPayPlan>  list );

    BillPayPlanBO   toBO(BillPayPlan  list );

    BillPayPlan  queryToDomain(BillPayPlanQuery query);

    BillPayPlan  paramToDomain(BillPayPlanParam param);
}

