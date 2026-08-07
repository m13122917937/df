-- 报价商品表（独立商品库，与现有商品主数据解耦）；价格固定三档：零售、分销1、分销2。
CREATE TABLE IF NOT EXISTS `quote_product` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `brand_id` bigint DEFAULT NULL COMMENT '品牌ID（引用 quote_brand）',
    `category_id` bigint DEFAULT NULL COMMENT '品类ID（引用 quote_category）',
    `brand` varchar(128) NOT NULL DEFAULT '' COMMENT '品牌',
    `category` varchar(128) NOT NULL DEFAULT '' COMMENT '品类',
    `product_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名',
    `spec_name` varchar(255) NOT NULL DEFAULT '' COMMENT '规格/型号',
    `retail_price` decimal(18,2) DEFAULT NULL COMMENT '零售价',
    `distributor1_price` decimal(18,2) DEFAULT NULL COMMENT '分销1价',
    `distributor2_price` decimal(18,2) DEFAULT NULL COMMENT '分销2价',
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
