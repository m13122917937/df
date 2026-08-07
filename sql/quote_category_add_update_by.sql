-- 为已存在的 quote_category 表补充 update_by（更新人）列（幂等，可重复执行）。
-- 品类不再使用图片，旧版 image_url 列可保留或手动删除：
--   ALTER TABLE `quote_category` DROP COLUMN `image_url`;

SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_category'
       AND COLUMN_NAME = 'update_by') = 0,
    'ALTER TABLE `quote_category`
     ADD COLUMN `update_by` varchar(64) NOT NULL DEFAULT '''' COMMENT ''更新人'' AFTER `sort_order`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
