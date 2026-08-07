-- 报价模块业务表；独立商品库，与 m_product_sku 完全隔离。

-- 报价价格档位表
CREATE TABLE IF NOT EXISTS `quote_price_tier` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tier_name` varchar(64) NOT NULL DEFAULT '' COMMENT '档位名称',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted` bigint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0-正常，1-删除)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_quote_price_tier_deleted` (`deleted`),
    KEY `idx_quote_price_tier_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价价格档位';

-- 报价商品表（独立商品库，与现有商品主数据解耦）
CREATE TABLE IF NOT EXISTS `quote_product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `brand` varchar(128) NOT NULL DEFAULT '' COMMENT '品牌',
    `category` varchar(128) NOT NULL DEFAULT '' COMMENT '品类',
    `product_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名',
    `spec_name` varchar(255) NOT NULL DEFAULT '' COMMENT '规格/型号',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
    `deleted` bigint NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0-正常，1-删除)',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_quote_product_brand` (`brand`),
    KEY `idx_quote_product_category` (`category`),
    KEY `idx_quote_product_deleted` (`deleted`),
    KEY `idx_quote_product_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价商品';

-- 报价商品价格明细表（商品 × 档位 × 价格，保存时按商品整体覆盖重建）
CREATE TABLE IF NOT EXISTS `quote_product_price` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id` bigint NOT NULL COMMENT '报价商品ID',
    `tier_id` bigint NOT NULL COMMENT '价格档位ID',
    `price` decimal(18,2) NOT NULL DEFAULT 0 COMMENT '价格',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_quote_product_price_product_tier` (`product_id`, `tier_id`),
    KEY `idx_quote_product_price_tier` (`tier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价商品价格明细';
