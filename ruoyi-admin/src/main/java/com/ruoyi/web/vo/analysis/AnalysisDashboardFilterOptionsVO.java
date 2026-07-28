package com.ruoyi.web.vo.analysis;

import lombok.Data;

import java.util.List;

/**
 * 经营统计基础数据筛选项响应对象。
 */
@Data
public class AnalysisDashboardFilterOptionsVO {

    /** 平台列表。 */
    private List<String> platforms;

    /** 店铺列表。 */
    private List<AnalysisStoreOptionVO> stores;

    /** 品牌列表。 */
    private List<String> brands;

    /** 品类列表。 */
    private List<String> categories;
}
