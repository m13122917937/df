-- 销售订单菜单SQL
-- 使用步骤：
-- 1. 先查询"需求管理"父菜单的 menu_id：
--    SELECT menu_id, menu_name FROM sys_menu WHERE menu_name LIKE '%需求管理%' OR menu_name LIKE '%需求%';
-- 2. 将下面SQL中的 @parentId 替换为上一步查到的 menu_id
-- 3. 执行以下SQL

-- ===== 修复已存在的销售订单菜单（修正菜单类型和组件路径）=====
UPDATE sys_menu
SET menu_type = 'M',
    component = 'demandManage/sale/index'
WHERE menu_name = '销售订单' AND parent_id = @parentId;

-- ===== 删除可能存在的重复菜单（之前INSERT导致的）=====
-- 如果有重复，只保留最新的那条（menu_id最大的）
DELETE FROM sys_menu
WHERE menu_name = '销售订单' AND parent_id = @parentId
  AND menu_id NOT IN (
    SELECT max_id FROM (
      SELECT MAX(menu_id) AS max_id FROM sys_menu WHERE menu_name = '销售订单' AND parent_id = @parentId
    ) AS tmp
  );

-- 获取销售订单菜单ID
SET @saleMenuId = (SELECT menu_id FROM sys_menu WHERE menu_name = '销售订单' AND parent_id = @parentId LIMIT 1);

-- ===== 插入子菜单（标签页），已存在的会忽略 =====
INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('当日发货', @saleMenuId, 1, 'today-shipped', 'demandManage/sale/today-shipped', 1, 0, 'C', '0', '0', 'sale:order:today-shipped', 'user', 'admin', NOW(), '', NULL, '销售订单-当日发货');

INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('在途', @saleMenuId, 2, 'transit', 'demandManage/sale/transit', 1, 0, 'C', '0', '0', 'sale:order:transit', 'user', 'admin', NOW(), '', NULL, '销售订单-在途');

INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('确认收货', @saleMenuId, 3, 'collected', 'demandManage/sale/collected', 1, 0, 'C', '0', '0', 'sale:order:collected', 'user', 'admin', NOW(), '', NULL, '销售订单-确认收货');

INSERT IGNORE INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('撤销订单', @saleMenuId, 4, 'return', 'demandManage/sale/return', 1, 0, 'C', '0', '0', 'sale:order:return', 'user', 'admin', NOW(), '', NULL, '销售订单-撤销订单');
