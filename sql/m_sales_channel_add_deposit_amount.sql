-- 销售渠道主数据新增人工维护的保证金金额。
ALTER TABLE `m_sales_channel`
    ADD COLUMN `deposit_amount` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '保证金金额'
        AFTER `subject_name`;
