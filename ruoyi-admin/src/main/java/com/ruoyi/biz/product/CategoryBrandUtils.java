package com.ruoyi.biz.product;

import java.util.*;

/**
 * 品类与品牌编码工具类
 */
public final class CategoryBrandUtils {

    // 私有构造器，防止实例化
    private CategoryBrandUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // 品类名称 -> 编码
    private static final Map<String, String> CATEGORY_TO_CODE = new HashMap<>();
    // 品类编码 -> 名称
    private static final Map<String, String> CODE_TO_CATEGORY = new HashMap<>();
    // 品牌名称 -> 编码
    private static final Map<String, String> BRAND_TO_CODE = new HashMap<>();
    // 品牌编码 -> 名称
    private static final Map<String, String> CODE_TO_BRAND = new HashMap<>();

    // 静态初始化块
    static {
        // 初始化品类映射
        CATEGORY_TO_CODE.put("手机", "SJ");
        CATEGORY_TO_CODE.put("穿戴", "CD");
        CATEGORY_TO_CODE.put("平板", "PB");
        CATEGORY_TO_CODE.put("笔记本", "PC");
        CATEGORY_TO_CODE.put("耳机", "EJ");
        CATEGORY_TO_CODE.put("家电", "JD");

        for (Map.Entry<String, String> entry : CATEGORY_TO_CODE.entrySet()) {
            CODE_TO_CATEGORY.put(entry.getValue(), entry.getKey());
        }

        // 初始化品牌映射
        BRAND_TO_CODE.put("华为", "HW");
        BRAND_TO_CODE.put("苹果", "PG");
        BRAND_TO_CODE.put("荣耀", "RY");
        BRAND_TO_CODE.put("小米", "XM");
        BRAND_TO_CODE.put("红米", "HM");
        BRAND_TO_CODE.put("iQOO", "IQ");
        BRAND_TO_CODE.put("OPPO", "OP");
        BRAND_TO_CODE.put("VIVO", "VI");
        BRAND_TO_CODE.put("WIKO", "WI");
        BRAND_TO_CODE.put("麦芒", "MM");
        BRAND_TO_CODE.put("中邮", "ZY");
        BRAND_TO_CODE.put("一加", "YJ");

        for (Map.Entry<String, String> entry : BRAND_TO_CODE.entrySet()) {
            CODE_TO_BRAND.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 根据品类名称获取品类编码
     *
     * @param categoryName 品类名称（如“手机”）
     * @return 对应的编码，若不存在则返回 null
     */
    public static String getCategoryCode(String categoryName) {
        return CATEGORY_TO_CODE.get(categoryName);
    }

    /**
     * 根据品类编码获取品类名称
     *
     * @param categoryCode 品类编码（如“SJ”）
     * @return 对应的名称，若不存在则返回 null
     */
    public static String getCategoryName(String categoryCode) {
        return CODE_TO_CATEGORY.get(categoryCode);
    }

    /**
     * 根据品牌名称获取品牌编码
     *
     * @param brandName 品牌名称（如“华为”）
     * @return 对应的编码，若不存在则返回 null
     */
    public static String getBrandCode(String brandName) {
        return BRAND_TO_CODE.get(brandName);
    }

    /**
     * 根据品牌编码获取品牌名称
     *
     * @param brandCode 品牌编码（如“HW”）
     * @return 对应的名称，若不存在则返回 null
     */
    public static String getBrandName(String brandCode) {
        return CODE_TO_BRAND.get(brandCode);
    }

    /**
     * 获取所有品类名称列表（不可变）
     */
    public static List<String> getAllCategoryNames() {
        return Collections.unmodifiableList(new ArrayList<>(CATEGORY_TO_CODE.keySet()));
    }

    /**
     * 获取所有品牌名称列表（不可变）
     */
    public static List<String> getAllBrandNames() {
        return Collections.unmodifiableList(new ArrayList<>(BRAND_TO_CODE.keySet()));
    }

    /**
     * 获取所有品类编码列表（不可变）
     */
    public static List<String> getAllCategoryCodes() {
        return Collections.unmodifiableList(new ArrayList<>(CODE_TO_CATEGORY.keySet()));
    }

    /**
     * 获取所有品牌编码列表（不可变）
     */
    public static List<String> getAllBrandCodes() {
        return Collections.unmodifiableList(new ArrayList<>(CODE_TO_BRAND.keySet()));
    }
}