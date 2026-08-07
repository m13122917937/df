-- 报价流水表：商品 × 日期 × 三档价格（零售、分销1、分销2）。
-- 每个商品每天一条报价记录，当天重复保存时覆盖当天价格；历史日期保留。
CREATE TABLE IF NOT EXISTS `quote_price_history` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id` bigint NOT NULL COMMENT '报价商品ID（引用 quote_product）',
    `quote_date` date NOT NULL COMMENT '报价日期',
    `retail_price` decimal(18,2) DEFAULT NULL COMMENT '零售价',
    `distributor1_price` decimal(18,2) DEFAULT NULL COMMENT '分销1价',
    `distributor2_price` decimal(18,2) DEFAULT NULL COMMENT '分销2价',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_quote_price_history_product_date` (`product_id`, `quote_date`),
    KEY `idx_quote_price_history_date` (`quote_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报价流水（每日报价）';
