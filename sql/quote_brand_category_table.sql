-- 报价模块品牌/品类主数据表；品牌、品类均支持配置图片。

-- 报价品牌表
CREATE TABLE IF NOT EXISTS `quote_brand` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `brand_name` varchar(128) NOT NULL DEFAULT '' COMMENT '品牌名称',
    `image_url` varchar(512) NOT NULL DEFAULT '' COMMENT '品牌图片',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted` bigint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0-正常，1-删除)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_quote_brand_name` (`brand_name`, `deleted`),
    KEY `idx_quote_brand_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价品牌';

-- 报价品类表
CREATE TABLE IF NOT EXISTS `quote_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_name` varchar(128) NOT NULL DEFAULT '' COMMENT '品类名称',
    `image_url` varchar(512) NOT NULL DEFAULT '' COMMENT '品类图片',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted` bigint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0-正常，1-删除)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_quote_category_name` (`category_name`, `deleted`),
    KEY `idx_quote_category_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价品类';
