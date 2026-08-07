-- 为已存在的 quote_brand 表补充 update_by（更新人）列（幂等，可重复执行）。

SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_brand'
       AND COLUMN_NAME = 'update_by') = 0,
    'ALTER TABLE `quote_brand`
     ADD COLUMN `update_by` varchar(64) NOT NULL DEFAULT '''' COMMENT ''更新人'' AFTER `sort_order`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
