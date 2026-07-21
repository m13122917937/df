-- =============================================
-- 已废弃：请执行 analysis_business_menu_level1_migration.sql。
-- 保留该文件仅作历史记录；不得创建“售后管理”分类。
-- 扣罚订单应归属：供应链 → 售后订单。
-- =============================================

-- 将“扣罚订单”放入既有的供应链“售后订单”分类。
SET @supplychain_root = (SELECT menu_id FROM sys_menu WHERE path = 'supplychain' AND parent_id = 0 LIMIT 1);
SET @afterSalesOrderMenuId = (
    SELECT menu_id FROM sys_menu WHERE menu_name = '售后订单' AND parent_id = @supplychain_root LIMIT 1
);
UPDATE sys_menu
SET parent_id = @afterSalesOrderMenuId,
    menu_name = '扣罚订单',
    order_num = 1,
    update_time = NOW()
WHERE @afterSalesOrderMenuId IS NOT NULL
  AND path = 'penalty'
  AND component = 'monery/penalty/index'
  AND perms = 'finance:deduction:list';
