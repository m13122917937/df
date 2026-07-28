-- 已存在的销售渠道表执行一次；新环境仅执行 m_sales_channel_table.sql 即可。
ALTER TABLE `m_sales_channel`
    DROP COLUMN `memo`;
