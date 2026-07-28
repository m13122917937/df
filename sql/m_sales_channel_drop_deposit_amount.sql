-- 已存在的销售渠道表执行一次；保证金改由经营分析的 ana_margin_config 独立维护。
ALTER TABLE `m_sales_channel`
    DROP COLUMN `deposit_amount`;

-- 删除销售渠道下历史遗留的保证金维护按钮权限。
DELETE FROM `sys_menu`
WHERE `perms` = 'master:salesChannel:edit'
  AND `del_flag` = '0';
