package com.ruoyi.analysis.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 经营分析常量。
 */
public final class AnalysisConstants {
    public static final String STATUS_COMPLETE = "COMPLETE";
    public static final String STATUS_INCOMPLETE = "INCOMPLETE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_FAILED = "FAILED";
    public static final String SYNC_DAILY = "DAILY";
    public static final String COST_SCOPE_DIRECT = "DIRECT";
    public static final String COST_SCOPE_INDIRECT = "INDIRECT";
    public static final String COST_SCOPE_DEPARTMENT = "DEPARTMENT";
    public static final String EXTRA_COST_SCOPE = "costScope";
    public static final String EXTRA_ANNUAL_RATE = "annualRate";
    public static final String EXTRA_HEADCOUNT = "headcount";

    public static final Set<String> CONFIG_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "FIXED_COEFFICIENT", "CASHBACK", "PENALTY", "PROMOTION", "MARGIN",
            "COLLECTION_DAYS", "INTERNAL_COST", "WAREHOUSE_COST", "SHOP_WHITELIST", "PLATFORM_FEE",
            "LOGISTICS", "IMPAIRMENT", "TAX", "OTHER_ADJUSTMENT")));

    private AnalysisConstants() {
    }
}
