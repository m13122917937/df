package com.ruoyi.wangdian.param.stock;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 入库单主信息
 */
@Data
@Builder
public class StockInInfoParam {

    private String outer_no;           // 外部单号
    private String src_order_no;      // 业务单号
    private String warehouse_no;      // 仓库编码
    private String logistics_no;      // 物流单号
    private String logistics_code;    // 物流编号
    private Integer is_check;         // 创建后的单据状态 (0:待审核, 1:已审核, 2:编辑中)
    private String reason;            // 入库原因
    private String remark;            // 备注
    private List<StockInInfoGoodsList> goods_list;  // 商品明细列表
}