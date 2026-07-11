package com.ruoyi.jky.param.reject;

import lombok.Data;

import java.util.List;

/**
 * 销售单驳回审核参数。
 */
@Data
public class RejectParam {

    private List<String> tradeNos;

    private String reason;

    private String isFreezeOrder;

}
