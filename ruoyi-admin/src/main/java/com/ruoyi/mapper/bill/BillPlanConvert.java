package com.ruoyi.mapper.bill;

import com.ruoyi.bill.domain.dto.PaymentOperationsDTO;
import com.ruoyi.bill.model.bo.BillBO;
import com.ruoyi.bill.model.bo.BillPayPlanBO;
import com.ruoyi.bill.model.bo.PaymentOperationsBO;
import com.ruoyi.bill.model.param.BillPayPlanParam;
import com.ruoyi.web.form.bill.PlanForm;
import com.ruoyi.web.vo.bill.BillPayPlanVO;
import com.ruoyi.web.vo.bill.BillPlanVO;
import com.ruoyi.web.vo.bill.BillSumVO;
import com.ruoyi.web.vo.bill.BillVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BillPlanConvert {


    BillPlanConvert INSTANCE = Mappers.getMapper(BillPlanConvert.class);


    BillPayPlanVO toVo(BillPayPlanBO billPayPlanBO);


    BillPayPlanParam toParam(PlanForm planForm);

}
