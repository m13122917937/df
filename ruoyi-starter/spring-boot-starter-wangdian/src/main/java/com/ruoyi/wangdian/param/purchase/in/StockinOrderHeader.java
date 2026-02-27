package com.ruoyi.wangdian.param.purchase.in;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockinOrderHeader {

    /**
     * 采购单号
     */
    private String purchase_no;

    /**
     * 仓库编号
     */
    private String warehouse_no;

    /**
     * 创建模式
     * 0: 编辑中
     * 1: 已提交/待审核
     * 2: 已审核
     */
    private Integer create_mode = 2;
}