package com.ruoyi.kuaidi100.model.e;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子面单V2 物品信息模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EOrderCargoInfo {
    private String name; // 物品名称
    private String count; // 数量
    private String unit; // 单位 (个, 千克)
    private String weight; // 重量
    private String type; // 物品类别
    private String code; // 物品编码
    private String remark; // 备注
}
