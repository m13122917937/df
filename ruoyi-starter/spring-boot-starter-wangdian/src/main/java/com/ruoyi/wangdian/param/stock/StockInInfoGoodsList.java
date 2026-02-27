package com.ruoyi.wangdian.param.stock;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品明细信息
 */
@Data
@Builder
public class StockInInfoGoodsList {


    private String spec_no;             // 商家编码
    private BigDecimal num;             // 入库数量 (Decimal(19,4))
    private String remark;              // 备注
    private String batch_no;            // 批次号
    private String position_no;         // 货位编号
    private String production_date;     // 生产日期
    private String expire_date;         // 有效期
    private Boolean defect;             // 残次品 (true/false)
    private BigDecimal stockin_price;   // 入库价 (Decimal(19,4))
    private List<String> sn_list;       // 序列号列表

}