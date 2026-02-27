package com.ruoyi.bill.convert;


import com.ruoyi.bill.domain.dto.BillSUMDTO;
import com.ruoyi.bill.domain.dto.PaymentOperationsDTO;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.bo.BillSUMBO;
import com.ruoyi.bill.model.bo.PaymentOperationsBO;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.bill.domain.Bill;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.query.BillQuery;
import com.ruoyi.bill.model.param.BillParam;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BillCov {

    BillCov INSTANCE = Mappers.getMapper(BillCov.class);


    List<BillBO>  listToBO(List<Bill>  list );

    BillBO   toBO(Bill  list );

    Bill  queryToDomain(BillQuery query);

    Bill  paramToDomain(BillParam param);


    List<PaymentOperationsBO> dtoTOBO(List<PaymentOperationsDTO> paymentOperationsDTOS);

    BillSUMBO toSumBO(BillSUMDTO billSUMDTOS);

}

