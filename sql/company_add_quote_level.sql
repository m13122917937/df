-- 为 u_company 表补充报价客户层级字段（幂等，可重复执行）。
-- 0-零售，1-批发1，2-批发2；未设置默认零售。

SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'u_company'
       AND COLUMN_NAME = 'quote_level') = 0,
    'ALTER TABLE `u_company`
     ADD COLUMN `quote_level` int NOT NULL DEFAULT 0 COMMENT ''报价客户层级(0-零售，1-批发1，2-批发2)''',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
