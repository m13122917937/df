package com.ruoyi.wangdian.param.purchase.in;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockinDetail {

    /**
     * 商家编码
     */
    private String spec_no;

    /**
     * 数量
     */
    private BigDecimal num;

    /**
     * 是否残次品
     */
    private Boolean defect;

    /**
     * 采购单位（辅助单位，默认为"无"）
     */
    private String unit_name;

    /**
     * 批次号
     */
    private String batch_no;

    /**
     * 有效期
     */
    private String expire_date;

    /**
     * 货位编号
     */
    private String position_no;

    /**
     * sn码（用英文逗号分隔）
     */
    private String sn_strings;

    /**
     * 生产日期
     */
    private String production_date;

    /**
     * 备注
     */
    private String remark;

    /**
     * 序列号信息（可选，用于辅助序列号管理）
     */
    private List<SnInfo> sn_infos;
}