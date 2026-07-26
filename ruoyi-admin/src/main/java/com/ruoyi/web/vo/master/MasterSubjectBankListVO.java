package com.ruoyi.web.vo.master;

import com.ruoyi.web.vo.bill.PayerVO;
import lombok.Data;

import java.util.List;

/**
 * 主体银行卡列表响应。
 */
@Data
public class MasterSubjectBankListVO {

    /**
     * 当前主体的默认银行卡ID，关联 m_subject_bank.id。
     */
    private Long defaultPayerId;

    /**
     * 主体下银行卡集合（按录入时间升序）。
     */
    private List<PayerVO> list;
}
