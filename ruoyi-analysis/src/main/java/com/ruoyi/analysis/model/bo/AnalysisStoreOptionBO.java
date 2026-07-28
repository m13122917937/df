package com.ruoyi.analysis.model.bo;

import lombok.Data;

/**
 * 经营统计店铺筛选项业务对象。
 */
@Data
public class AnalysisStoreOptionBO {

    /** 销售渠道主键。 */
    private Long channelId;

    /** 平台名称。 */
    private String platformName;

    /** 店铺名称。 */
    private String shopName;
}
