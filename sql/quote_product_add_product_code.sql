-- 为 quote_product 表补充商品编码字段（幂等，可重复执行）。

SET @sql := IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'quote_product'
       AND COLUMN_NAME = 'product_code') = 0,
    'ALTER TABLE `quote_product`
     ADD COLUMN `product_code` varchar(128) NOT NULL DEFAULT '''' COMMENT ''商品编码'' AFTER `spec_name`',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
