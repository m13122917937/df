-- ============================================================
-- 供应链 → 合规经营（合同管理、发票管理）
-- ============================================================

-- Step 1: 获取 供应链 的 menu_id
SET @mod2 = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='供应链' AND parent_id=0 LIMIT 1) t);

-- Step 2: 创建 合规经营 二级分类（M 目录 → Frontend 会解析为 ParentView）
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('合规经营', @mod2, 5, 'compliance', '', 1, 0, 'M', '0', '0', '', 'tree', 'admin', NOW(), '', NULL, '');

SET @cat_compliance = (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='合规经营' AND parent_id=@mod2 LIMIT 1) t);

-- Step 3: 将 合同管理 移到 合规经营 下
UPDATE sys_menu SET parent_id=@cat_compliance, update_time=NOW()
WHERE menu_name='合同管理';

-- Step 4: 新增 发票管理 三级页面
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('发票管理', @cat_compliance, 10, 'invoice', 'financialManage/invoice/index', 1, 0, 'C', '0', '0', 'finance:invoice:list', 'dict', 'admin', NOW(), '', NULL, '发票管理菜单');

SET @invoiceMenuId := LAST_INSERT_ID();

-- Step 5: 发票管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  ('发票查询', @invoiceMenuId, 1, '', null, 1, 0, 'F', '0', '0', 'finance:invoice:list',   '#', 'admin', NOW(), '', NULL, ''),
  ('发票新增', @invoiceMenuId, 2, '', null, 1, 0, 'F', '0', '0', 'finance:invoice:add',    '#', 'admin', NOW(), '', NULL, ''),
  ('发票修改', @invoiceMenuId, 3, '', null, 1, 0, 'F', '0', '0', 'finance:invoice:edit',   '#', 'admin', NOW(), '', NULL, ''),
  ('发票删除', @invoiceMenuId, 4, '', null, 1, 0, 'F', '0', '0', 'finance:invoice:remove', '#', 'admin', NOW(), '', NULL, '');

-- Step 6: 验证
SELECT
  m1.menu_name AS '一级模块',
  m2.menu_name AS '二级分类',
  m3.menu_name AS '三级页面'
FROM sys_menu m1
JOIN sys_menu m2 ON m2.parent_id = m1.menu_id
LEFT JOIN sys_menu m3 ON m3.parent_id = m2.menu_id
WHERE m1.parent_id = 0 AND m1.menu_name='供应链'
ORDER BY m2.order_num, m3.order_num;
