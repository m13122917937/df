package com.ruoyi.wangdian.param.purchase.in;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnInfo {

    /**
     * 序列号
     */
    private String sn;

    /**
     * 辅助序列号
     */
    private String second_sn;
}