-- 已废弃：请执行 analysis_business_menu_level1_migration.sql。
-- 此文件仅保留旧环境兼容记录，禁止用于新增菜单；不得迁移供应链售后“扣罚列表”。
-- 核算配置菜单结构迁移（仅修改 sys_menu，不改业务表）
-- 执行后请重新登录，或清理菜单缓存后刷新页面。

SET @legacy_analysis_root = (SELECT menu_id FROM sys_menu WHERE path = 'operationsManage' AND parent_id = 0 LIMIT 1);

UPDATE sys_menu SET parent_id = 0, menu_name = '经营分析', order_num = 65
WHERE path = 'businessAnalysis' AND parent_id = @legacy_analysis_root;
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营分析', 0, 65, 'businessAnalysis', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '经营数据、利润和成本分析'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'businessAnalysis' AND parent_id = 0);
SET @analysis_parent = (SELECT menu_id FROM sys_menu WHERE path = 'businessAnalysis' AND parent_id = 0 LIMIT 1);

UPDATE sys_menu SET parent_id = @analysis_parent
WHERE parent_id = @legacy_analysis_root AND path IN ('databoard', 'accounting');
SET @analysis_config = (SELECT menu_id FROM sys_menu WHERE path = 'accounting' AND parent_id = @analysis_parent LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '商品收益与成本', @analysis_config, 1, 'productAccounting', NULL, 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '商品收益与商品成本设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'productAccounting' AND parent_id = @analysis_config);
SET @analysis_product = (SELECT menu_id FROM sys_menu WHERE path = 'productAccounting' AND parent_id = @analysis_config LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营费用', @analysis_config, 2, 'operatingExpense', NULL, 1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '订单履约与日常经营费用设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'operatingExpense' AND parent_id = @analysis_config);
SET @analysis_expense = (SELECT menu_id FROM sys_menu WHERE path = 'operatingExpense' AND parent_id = @analysis_config LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '资金与人员成本', @analysis_config, 3, 'organizationCost', NULL, 1, 0, 'M', '0', '0', '', 'peoples', 'admin', NOW(), '资金占用、人员和仓配成本设置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'organizationCost' AND parent_id = @analysis_config);
SET @analysis_organization = (SELECT menu_id FROM sys_menu WHERE path = 'organizationCost' AND parent_id = @analysis_config LIMIT 1);

UPDATE sys_menu SET parent_id = @analysis_product, menu_name = '返利与价保', order_num = 1, remark = '返利活动与价格保障'
WHERE path = 'rebateProtection';
UPDATE sys_menu SET parent_id = @analysis_product, menu_name = '商品成本系数', order_num = 2, remark = '按商品、平台和店铺设置成本系数'
WHERE path = 'fixedCoefficient';
UPDATE sys_menu SET parent_id = @analysis_product, menu_name = '销售返现', order_num = 3, remark = '订单或商品销售返现'
WHERE path = 'cashback';

UPDATE sys_menu SET parent_id = @analysis_expense, menu_name = '平台服务费', order_num = 1, remark = '平台服务与技术服务费用'
WHERE path = 'platformFee';
UPDATE sys_menu SET parent_id = @analysis_expense, menu_name = '发货物流费', order_num = 2, remark = '订单发货和物流费用'
WHERE path = 'logisticsFee';
UPDATE sys_menu SET parent_id = @analysis_expense, menu_name = '推广投放费', order_num = 3, remark = '店铺和商品推广投放费用'
WHERE path = 'promotion';
UPDATE sys_menu SET parent_id = @analysis_expense, menu_name = '税费', order_num = 4, remark = '经营产生的税费'
WHERE path = 'tax';
UPDATE sys_menu SET parent_id = @analysis_expense, menu_name = '其他调整', order_num = 5, remark = '未归类的经营收入或费用调整'
WHERE path = 'otherAdjustment';

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '平台服务费', @analysis_expense, 1, 'platformFee', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'money', 'admin', NOW(), '平台服务与技术服务费用'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'platformFee' AND parent_id = @analysis_expense);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '发货物流费', @analysis_expense, 2, 'logisticsFee', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'guide', 'admin', NOW(), '订单发货和物流费用'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'logisticsFee' AND parent_id = @analysis_expense);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '税费', @analysis_expense, 4, 'tax', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'documentation', 'admin', NOW(), '经营产生的税费'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'tax' AND parent_id = @analysis_expense);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '其他调整', @analysis_expense, 5, 'otherAdjustment', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'edit', 'admin', NOW(), '未归类的经营收入或费用调整'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'otherAdjustment' AND parent_id = @analysis_expense);

UPDATE sys_menu SET parent_id = @analysis_organization, menu_name = '保证金', order_num = 1, remark = '平台或店铺保证金设置'
WHERE path = 'margin';
UPDATE sys_menu SET parent_id = @analysis_organization, menu_name = '回款周期', order_num = 2, remark = '货款、补贴回款周期设置'
WHERE path = 'collectionDays';
UPDATE sys_menu SET parent_id = @analysis_organization, menu_name = '人员成本', order_num = 3, remark = '运营、采购、客服等人员成本'
WHERE path = 'internalCost';
UPDATE sys_menu SET parent_id = @analysis_organization, menu_name = '仓配成本', order_num = 4, remark = '仓储和配送相关成本'
WHERE path = 'warehouseCost';

DELETE FROM sys_menu WHERE parent_id IN (
    SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE path = 'nativeBi' AND parent_id = @legacy_analysis_root) AS analysis_native_bi
);
DELETE FROM sys_menu WHERE path = 'nativeBi' AND parent_id = @legacy_analysis_root;
