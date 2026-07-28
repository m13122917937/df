-- 删除 经营指标树 菜单及关联的按钮/权限
-- 菜单路径: businessAnalysis/databoard/metricTree
-- 注意: 如果子菜单或按钮引用了该菜单的 menu_id，请先删除子菜单

-- 删除角色-菜单关联
DELETE FROM sys_role_menu WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE path = 'metricTree');

-- 删除菜单
DELETE FROM sys_menu WHERE path = 'metricTree';
