-- =============================================
-- 售后管理菜单 SQL
-- 1. 新增"售后管理"顶级菜单（与需求管理/财务管理同级）
-- 2. 将"扣罚列表"从财务管理移到售后管理下
-- 3. 新增"销售退货"子菜单（占位）
-- =============================================

-- Step 1: 创建"售后管理"顶级目录（如果不存在）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '售后管理', 0, 55, 'afterSales', null, 1, 0, 'M', '0', '0', '', 'service', 'admin', NOW(), '', NULL, '售后管理目录'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '售后管理' AND parent_id = 0);

-- Step 2: 获取售后管理的menu_id
SET @afterSalesMenuId = (SELECT menu_id FROM sys_menu WHERE menu_name = '售后管理' AND parent_id = 0 LIMIT 1);

-- Step 3: 将"扣罚列表"从原来的位置移到售后管理下
UPDATE sys_menu
SET parent_id = @afterSalesMenuId,
    order_num = 1,
    update_time = NOW()
WHERE menu_name = '扣罚列表';

-- 如果没有找到名为"扣罚列表"的菜单，则新增一个
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '扣罚列表', @afterSalesMenuId, 1, 'penalty', 'monery/penalty/index', 1, 0, 'C', '0', '0', 'finance:deduction:list', 'edit', 'admin', NOW(), '', NULL, '扣罚列表菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '扣罚列表');

-- Step 4: 新增"销售退货"子菜单（占位）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '销售退货', @afterSalesMenuId, 2, 'sales-return', 'afterSales/return/index', 1, 0, 'C', '0', '0', 'after:sales:return:list', 'user', 'admin', NOW(), '', NULL, '销售退货菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '销售退货' AND parent_id = @afterSalesMenuId);
