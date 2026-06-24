package com.ruoyi.jky.param.reject;

import lombok.Data;

/**
 * 销售单驳回审核参数。
 */
@Data
public class RejectParam {

    private String tradeNos;

    private String reason;

    private String isFreezeOrder;

}
