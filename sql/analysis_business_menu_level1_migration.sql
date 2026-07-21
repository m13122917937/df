-- 经营分析三级菜单迁移（仅修改 sys_menu，不改业务表）
-- 目标结构：经营分析 → 数据看板 / 商品成本 / 经营费用 / 资金人力 → 具体页面。
-- 注意：供应链“扣罚订单”不属于经营分析；本脚本会恢复到既有的售后订单分类。
-- 执行后请重新登录，或清理菜单缓存后刷新页面。

SET @legacy_root = (SELECT menu_id FROM sys_menu WHERE path = 'operationsManage' AND parent_id = 0 LIMIT 1);

-- 将此前嵌套在运营管理下的经营分析提升为一级菜单。
UPDATE sys_menu SET parent_id = 0, menu_name = '经营分析', order_num = 65
WHERE path = 'businessAnalysis' AND parent_id = @legacy_root;
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营分析', 0, 65, 'businessAnalysis', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '经营数据、利润和成本分析'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'businessAnalysis' AND parent_id = 0);
DELETE duplicated_menu
FROM sys_menu duplicated_menu
INNER JOIN sys_menu retained_menu
    ON duplicated_menu.path = retained_menu.path
    AND duplicated_menu.menu_id > retained_menu.menu_id
WHERE duplicated_menu.path = 'businessAnalysis';
SET @analysis_root = (SELECT menu_id FROM sys_menu WHERE path = 'businessAnalysis' AND parent_id = 0 LIMIT 1);

-- 数据看板作为经营分析的二级菜单。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '数据看板', @analysis_root, 1, 'databoard', NULL, 1, 0, 'M', '0', '0', '', 'dashboard', 'admin', NOW(), '经营数据看板'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'databoard' AND parent_id = @analysis_root);
UPDATE sys_menu SET parent_id = @analysis_root, menu_name = '数据看板', order_num = 1
WHERE path = 'databoard';
DELETE duplicated_menu
FROM sys_menu duplicated_menu
INNER JOIN sys_menu retained_menu
    ON duplicated_menu.path = retained_menu.path
    AND duplicated_menu.menu_id > retained_menu.menu_id
WHERE duplicated_menu.path = 'databoard';
SET @dashboard_menu = (SELECT menu_id FROM sys_menu WHERE path = 'databoard' AND parent_id = @analysis_root LIMIT 1);
UPDATE sys_menu SET parent_id = @dashboard_menu
WHERE path IN ('operationStats', 'performanceRollup', 'channelProduction', 'humanEfficiency',
               'metricTree', 'orderDetails');

-- 商品成本、经营费用、资金与人力成本均为经营分析的二级菜单。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '商品成本', @analysis_root, 2, 'productAccounting', NULL, 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '商品收益、返利和成本设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'productAccounting' AND parent_id = @analysis_root);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营费用', @analysis_root, 3, 'operatingExpense', NULL, 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '履约、推广和日常经营费用设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'operatingExpense' AND parent_id = @analysis_root);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '资金人力', @analysis_root, 4, 'organizationCost', NULL, 1, 0, 'M', '0', '0', '', 'peoples', 'admin', NOW(), '保证金、回款、人员和仓配成本设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'organizationCost' AND parent_id = @analysis_root);

-- 已有分类菜单提升为二级菜单；不存在时由上面的 INSERT 创建。
UPDATE sys_menu SET parent_id = @analysis_root, menu_name = '商品成本', order_num = 2
WHERE path = 'productAccounting';
UPDATE sys_menu SET parent_id = @analysis_root, menu_name = '经营费用', order_num = 3
WHERE path = 'operatingExpense';
UPDATE sys_menu SET parent_id = @analysis_root, menu_name = '资金人力', order_num = 4
WHERE path = 'organizationCost';

DELETE duplicated_menu
FROM sys_menu duplicated_menu
INNER JOIN sys_menu retained_menu
    ON duplicated_menu.path = retained_menu.path
    AND duplicated_menu.menu_id > retained_menu.menu_id
WHERE duplicated_menu.path IN ('productAccounting', 'operatingExpense', 'organizationCost');
SET @product_menu = (SELECT menu_id FROM sys_menu WHERE path = 'productAccounting' AND parent_id = @analysis_root LIMIT 1);
SET @expense_menu = (SELECT menu_id FROM sys_menu WHERE path = 'operatingExpense' AND parent_id = @analysis_root LIMIT 1);
SET @organization_menu = (SELECT menu_id FROM sys_menu WHERE path = 'organizationCost' AND parent_id = @analysis_root LIMIT 1);

-- 将原有平铺菜单和原四级菜单归入对应二级分类。
UPDATE sys_menu SET parent_id = @product_menu, menu_name = '成本系数', order_num = 2 WHERE path = 'fixedCoefficient';

UPDATE sys_menu SET parent_id = @expense_menu, menu_name = '平台服务费', order_num = 1 WHERE path = 'platformFee';
UPDATE sys_menu SET parent_id = @expense_menu, menu_name = '发货物流费', order_num = 2 WHERE path = 'logisticsFee';
UPDATE sys_menu SET parent_id = @expense_menu, menu_name = '推广投放费', order_num = 3 WHERE path = 'promotion';
UPDATE sys_menu SET parent_id = @expense_menu, menu_name = '税费', order_num = 4 WHERE path = 'tax';
UPDATE sys_menu SET parent_id = @expense_menu, menu_name = '其他调整', order_num = 5 WHERE path = 'otherAdjustment';

UPDATE sys_menu SET parent_id = @organization_menu, menu_name = '保证金', order_num = 1 WHERE path = 'margin';
UPDATE sys_menu SET parent_id = @organization_menu, menu_name = '回款周期', order_num = 2 WHERE path = 'collectionDays';
UPDATE sys_menu SET parent_id = @organization_menu, menu_name = '人员成本', order_num = 3 WHERE path = 'internalCost';
UPDATE sys_menu SET parent_id = @organization_menu, menu_name = '仓配成本', order_num = 4 WHERE path = 'warehouseCost';

-- 补齐旧版菜单中没有的费用页面。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '平台服务费', @expense_menu, 1, 'platformFee', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'money', 'admin', NOW(), '平台服务与技术服务费用'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'platformFee' AND parent_id = @expense_menu);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '发货物流费', @expense_menu, 2, 'logisticsFee', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'guide', 'admin', NOW(), '订单发货和物流费用'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'logisticsFee' AND parent_id = @expense_menu);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '税费', @expense_menu, 4, 'tax', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'documentation', 'admin', NOW(), '经营产生的税费'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'tax' AND parent_id = @expense_menu);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '其他调整', @expense_menu, 5, 'otherAdjustment', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'edit', 'admin', NOW(), '未归类的经营收入或费用调整'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'otherAdjustment' AND parent_id = @expense_menu);

-- 修复此前错误迁移：供应链 → 售后订单 → 扣罚订单。
SET @supplychain_root = (SELECT menu_id FROM sys_menu WHERE path = 'supplychain' AND parent_id = 0 LIMIT 1);
SET @after_sales_order_menu = (
    SELECT menu_id FROM sys_menu WHERE menu_name = '售后订单' AND parent_id = @supplychain_root LIMIT 1
);
UPDATE sys_menu
SET parent_id = @after_sales_order_menu, menu_name = '扣罚订单', order_num = 1, update_time = NOW()
WHERE @after_sales_order_menu IS NOT NULL
  AND path = 'penalty'
  AND component = 'monery/penalty/index'
  AND perms = 'finance:deduction:list';

-- 删除本迁移此前错误创建的空“售后管理”分类，不影响已有业务菜单。
SET @mistaken_after_sales_menu = (
    SELECT menu_id FROM sys_menu WHERE path = '/aftersales' AND parent_id = @supplychain_root LIMIT 1
);
DELETE FROM sys_menu
WHERE menu_id = @mistaken_after_sales_menu
  AND NOT EXISTS (
      SELECT 1 FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @mistaken_after_sales_menu) AS after_sales_children
  );

-- 不再提供“扣罚订单”“资损处理”两项经营分析配置菜单。
DELETE FROM sys_menu
WHERE path IN ('penalty', 'impairment')
  AND component = 'operationsManage/config/index'
  AND perms = 'analysis:config:list';

-- 数据质量、同步日志仅作为后台运维能力保留，不显示为用户菜单。
DELETE FROM sys_menu
WHERE (path = 'dataQuality' AND component = 'operationsManage/dashboard/index' AND perms = 'analysis:dashboard:list')
   OR (path = 'syncLogs' AND component = 'operationsManage/syncLogs/index' AND perms = 'analysis:sync:logs');

-- 清理已不使用的“核算配置”和“原生BI”中间层，以及其隐藏按钮权限菜单。
-- 同一路径只保留一条菜单，解决旧脚本重复创建菜单的问题。
UPDATE sys_menu SET parent_id = 0
WHERE path = 'businessAnalysis';
DELETE duplicated_menu
FROM sys_menu duplicated_menu
INNER JOIN sys_menu retained_menu
    ON duplicated_menu.path = retained_menu.path
    AND duplicated_menu.menu_id > retained_menu.menu_id
WHERE duplicated_menu.path IN (
    'fixedCoefficient',
    'platformFee', 'logisticsFee', 'promotion', 'tax', 'otherAdjustment',
    'margin', 'collectionDays', 'internalCost', 'warehouseCost'
);
DELETE FROM sys_menu WHERE perms IN ('analysis:config:edit', 'analysis:config:import', 'analysis:config:export');
DELETE FROM sys_menu WHERE parent_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE path = 'nativeBi') AS native_bi);
DELETE FROM sys_menu WHERE path IN ('nativeBi', 'accounting');
