package com.ruoyi.analysis.model.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 经营统计筛选项业务对象。
 */
@Data
public class AnalysisDashboardFilterOptionsBO {

    /** 平台列表。 */
    private List<String> platforms = new ArrayList<>();

    /** 店铺列表。 */
    private List<AnalysisStoreOptionBO> stores = new ArrayList<>();

    /** 品牌列表。 */
    private List<String> brands = new ArrayList<>();

    /** 品类列表。 */
    private List<String> categories = new ArrayList<>();
}
