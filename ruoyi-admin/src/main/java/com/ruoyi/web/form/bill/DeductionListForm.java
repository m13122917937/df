package com.ruoyi.web.form.bill;

import lombok.Data;

import java.util.List;

@Data

public class DeductionListForm {


    /**
     * 供应商id
     */

    private Long companyId;

    /**
     * 订单编号
     *
     */

    private List<String> orderCodeList;

    /**
     * 商家单号
     */

    private List<String> originalOrderIdList;


}
