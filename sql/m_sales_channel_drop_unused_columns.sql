-- 销售渠道主数据移除无业务用途的仓库、联系与地址字段。
-- 执行前请确认当前 m_sales_channel 表仍包含下列字段；新建环境仅执行 m_sales_channel_table.sql 即可。
ALTER TABLE `m_sales_channel`
    DROP COLUMN `warehouse_code`,
    DROP COLUMN `warehouse_name`,
    DROP COLUMN `contact_name`,
    DROP COLUMN `contact_phone`,
    DROP COLUMN `email`,
    DROP COLUMN `address`,
    DROP COLUMN `country_name`,
    DROP COLUMN `province_name`,
    DROP COLUMN `city_name`,
    DROP COLUMN `town_name`;
