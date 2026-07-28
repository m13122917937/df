-- 商品 SKU 主数据表迁移：p_product_sku -> m_product_sku。
-- 执行前请在目标数据库备份；脚本不复制、不清理任何商品数据。
SET @old_table_exists := (
    SELECT COUNT(1)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'p_product_sku'
);
SET @new_table_exists := (
    SELECT COUNT(1)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'm_product_sku'
);

SET @migration_sql := CASE
    WHEN @old_table_exists = 1 AND @new_table_exists = 0
        THEN 'RENAME TABLE `p_product_sku` TO `m_product_sku`'
    WHEN @old_table_exists = 0 AND @new_table_exists = 1
        THEN 'SELECT ''m_product_sku already exists; migration skipped'' AS message'
    WHEN @old_table_exists = 1 AND @new_table_exists = 1
        THEN 'SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''Both p_product_sku and m_product_sku exist; verify data before migration'''
    ELSE 'SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''Neither p_product_sku nor m_product_sku exists'''
END;

PREPARE product_sku_migration_stmt FROM @migration_sql;
EXECUTE product_sku_migration_stmt;
DEALLOCATE PREPARE product_sku_migration_stmt;
