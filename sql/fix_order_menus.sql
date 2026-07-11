-- ============================================================
-- 修复：将缺失的订单菜单恢复到 供应链 → 订单管理 下
-- 从 sys_menu_copy（备份表）恢复被误删的菜单项
-- ============================================================

-- ============================================================
-- Step 1: 修复 订单管理 的路径（去掉前导 /）
-- ============================================================
UPDATE sys_menu SET path = 'orders' WHERE menu_id = 2086 AND path = '/orders';

-- ============================================================
-- Step 2: 插入缺失的 C 级菜单到 订单管理(2086)
-- ============================================================

-- 代发订单
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '代发订单', 2086, 1, 'wholesale', 'demandManage/wholesale/index', 1, 0, 'C', '0', '0', '', 'tool', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '代发订单' AND parent_id = 2086);
SET @new_daifa = (SELECT menu_id FROM sys_menu WHERE menu_name = '代发订单' AND parent_id = 2086);

-- 入仓订单
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '入仓订单', 2086, 2, 'putin', 'demandManage/putin/index', 1, 0, 'C', '0', '0', '', 'list', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '入仓订单' AND parent_id = 2086);
SET @new_rucang = (SELECT menu_id FROM sys_menu WHERE menu_name = '入仓订单' AND parent_id = 2086);

-- 销售订单
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '销售订单', 2086, 3, 'sale', 'demandManage/sale/index', 1, 0, 'C', '0', '0', '', 'dict', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '销售订单' AND parent_id = 2086);
SET @new_xiaoshou = (SELECT menu_id FROM sys_menu WHERE menu_name = '销售订单' AND parent_id = 2086);

-- 全部订单
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '全部订单', 2086, 4, 'allOrders', 'demandManage/allOrders/index', 1, 0, 'C', '0', '0', '', 'form', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '全部订单' AND parent_id = 2086);
SET @new_quanbu = (SELECT menu_id FROM sys_menu WHERE menu_name = '全部订单' AND parent_id = 2086);

-- 产品映射
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '产品映射', 2086, 5, 'productMapping', 'demandManage/productMapping/index', 1, 0, 'C', '0', '0', '', 'skill', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '产品映射' AND parent_id = 2086);
SET @new_chanpin = (SELECT menu_id FROM sys_menu WHERE menu_name = '产品映射' AND parent_id = 2086);

-- ============================================================
-- Step 3: 将 拣货入仓 从 仓库管理 移到 订单管理
-- ============================================================
UPDATE sys_menu SET parent_id = 2086, order_num = 6 WHERE menu_id = 2047 AND parent_id = 2046;

-- ============================================================
-- Step 4: 插入 集采、商品管理（新建菜单，需在后台菜单管理补充完整配置）
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '集采', 2086, 7, 'pool', 'demandManage/pool/index', 1, 0, 'C', '0', '0', '', 'list', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '集采' AND parent_id = 2086);

INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '商品管理', 2086, 8, 'product', 'demandManage/product/index', 1, 0, 'C', '0', '0', '', 'skill', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '商品管理' AND parent_id = 2086);

-- ============================================================
-- Step 5: 插入 代发订单 下的 F 级按钮权限
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT smc.menu_name, @new_daifa, smc.order_num, smc.path, smc.component, smc.is_frame, smc.is_cache, smc.menu_type, smc.visible, smc.status, smc.perms, smc.icon, 'admin', NOW()
FROM sys_menu_copy smc WHERE smc.parent_id = 2001
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.menu_name = smc.menu_name AND s.parent_id = @new_daifa);

-- ============================================================
-- Step 6: 插入 入仓订单 下的 F 级按钮权限
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT smc.menu_name, @new_rucang, smc.order_num, smc.path, smc.component, smc.is_frame, smc.is_cache, smc.menu_type, smc.visible, smc.status, smc.perms, smc.icon, 'admin', NOW()
FROM sys_menu_copy smc WHERE smc.parent_id = 2002
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.menu_name = smc.menu_name AND s.parent_id = @new_rucang);

-- ============================================================
-- Step 7: 插入 销售订单 下的 F 级按钮权限
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT smc.menu_name, @new_xiaoshou, smc.order_num, smc.path, smc.component, smc.is_frame, smc.is_cache, smc.menu_type, smc.visible, smc.status, smc.perms, smc.icon, 'admin', NOW()
FROM sys_menu_copy smc WHERE smc.parent_id = 2062
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.menu_name = smc.menu_name AND s.parent_id = @new_xiaoshou);

-- ============================================================
-- Step 8: 插入 全部订单 下的 F 级按钮权限
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT smc.menu_name, @new_quanbu, smc.order_num, smc.path, smc.component, smc.is_frame, smc.is_cache, smc.menu_type, smc.visible, smc.status, smc.perms, smc.icon, 'admin', NOW()
FROM sys_menu_copy smc WHERE smc.parent_id = 2003
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.menu_name = smc.menu_name AND s.parent_id = @new_quanbu);

-- ============================================================
-- Step 9: 插入 产品映射 下的 F 级按钮权限
-- ============================================================
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT smc.menu_name, @new_chanpin, smc.order_num, smc.path, smc.component, smc.is_frame, smc.is_cache, smc.menu_type, smc.visible, smc.status, smc.perms, smc.icon, 'admin', NOW()
FROM sys_menu_copy smc WHERE smc.parent_id = 2031
  AND NOT EXISTS (SELECT 1 FROM sys_menu s WHERE s.menu_name = smc.menu_name AND s.parent_id = @new_chanpin);

-- ============================================================
-- Step 10: 验证结果
-- ============================================================
SELECT '订单管理 下的所有菜单:' AS info;
SELECT m.menu_id, m.menu_name, m.menu_type, m.path, m.component, m.order_num
FROM sys_menu m WHERE m.parent_id = 2086 ORDER BY m.order_num;
