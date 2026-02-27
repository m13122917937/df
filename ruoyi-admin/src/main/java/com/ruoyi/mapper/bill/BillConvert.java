package com.ruoyi.mapper.bill;

import com.ruoyi.bill.domain.dto.PaymentOperationsDTO;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.bo.BillSUMBO;
import com.ruoyi.bill.model.bo.PaymentOperationsBO;
import com.ruoyi.bill.model.param.BillParam;
import com.ruoyi.web.vo.bill.BillPlanVO;
import com.ruoyi.web.vo.bill.BillSumVO;
import com.ruoyi.web.vo.bill.BillVO;
import com.ruoyi.web.vo.bill.SumBillVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BillConvert {


    BillConvert INSTANCE = Mappers.getMapper(BillConvert.class);


    List<BillSumVO> toVO(List<PaymentOperationsBO> data);

    List<PaymentOperationsBO> dtoTOBO(List<PaymentOperationsDTO> paymentOperationsDTOS);

    List<BillPlanVO> billPayPlantoVOList(List<BillPayPlanBO> billPayPlanBOS);

    List<BillVO> toBillVOList(List<BillBO> data);


    SumBillVO toSum(BillSUMBO sum);


    BillParam toBillParam(BillBO billBO);


}
