package com.ruoyi.jky.rep.fin;

import lombok.Data;

import java.util.List;

/**
 * 开放平台收付款单创建返回
 */
@Data
public class CreateCashOrCostRecPayBillRep {

    /** 系统单据号 */
    private String billNum;

    /** 单据状态：1-草稿，2-待审核，3-已审核，4-待修改，5-已创建凭证 */
    private Integer billStatus;

    /** 单据 ID */
    private Long id;

    /** 业务明细返回列表 */
    private List<BillTransDetailCreateVo> billTransDetailCreateVoList;

    @Data
    public static class BillTransDetailCreateVo {

        /** 明细 ID */
        private Long id;

        /** 行号 */
        private Integer lineNo;
    }
}
