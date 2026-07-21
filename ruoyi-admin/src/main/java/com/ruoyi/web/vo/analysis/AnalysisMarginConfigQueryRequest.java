package com.ruoyi.web.vo.analysis;

import lombok.Data;

/**
 * 保证金配置查询请求。
 */
@Data
public class AnalysisMarginConfigQueryRequest {
    private String platform;
    private String shopName;
}
