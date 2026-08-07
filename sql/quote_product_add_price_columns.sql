-- 为已存在的 quote_product 表补充三档价格列（幂等，可重复执行）。
-- 全新环境直接执行 quote_product_table.sql 即可，无需本脚本。

-- 1. 补充 retail_price 列
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'retail_price') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `retail_price` decimal(18,2) DEFAULT NULL COMMENT ''零售价'' AFTER `spec_name`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 补充 distributor1_price 列
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'distributor1_price') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `distributor1_price` decimal(18,2) DEFAULT NULL COMMENT ''分销1价'' AFTER `retail_price`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 补充 distributor2_price 列
SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'distributor2_price') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `distributor2_price` decimal(18,2) DEFAULT NULL COMMENT ''分销2价'' AFTER `distributor1_price`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================
-- 旧版表清理说明（可选，确认无依赖后再执行）：
-- 价格档位表与价格明细表在新版中已不再使用，可保留或手动删除：
--   DROP TABLE IF EXISTS `quote_product_price`;
--   DROP TABLE IF EXISTS `quote_price_tier`;
-- ============================================================
