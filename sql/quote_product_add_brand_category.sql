-- 为已存在的 quote_product 表补充 brand_id / category_id 列（幂等，可重复执行）。
-- 适用于之前已按旧版脚本建表的环境；全新环境直接执行 quote_product_table.sql 即可。

-- 1. 补充 brand_id 列
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'brand_id') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `brand_id` bigint DEFAULT NULL COMMENT ''品牌ID（引用 quote_brand）'' AFTER `id`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 补充 category_id 列
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'category_id') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `category_id` bigint DEFAULT NULL COMMENT ''品类ID（引用 quote_category）'' AFTER `brand_id`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 为两列补充索引（若不存在）
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND INDEX_NAME = 'idx_quote_product_brand_id') = 0,
    'ALTER TABLE `quote_product` ADD INDEX `idx_quote_product_brand_id` (`brand_id`)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND INDEX_NAME = 'idx_quote_product_category_id') = 0,
    'ALTER TABLE `quote_product` ADD INDEX `idx_quote_product_category_id` (`category_id`)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
