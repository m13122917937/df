package com.ruoyi.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.bill.domain.dto.BillSUMDTO;
import com.ruoyi.bill.domain.dto.PaymentOperationsDTO;
import com.ruoyi.bill.model.query.BillQuery;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.bill.domain.Bill;

/**
 * 账单Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    BillSUMDTO selectBillSum(BillQuery query);


    List<PaymentOperationsDTO> paymentOperations(BillQuery billQuery);

}
