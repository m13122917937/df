-- 删除返利管理(rebate)的菜单及角色关联

-- 1. 删除角色关联
DELETE FROM sys_role_menu WHERE menu_id IN (
  SELECT menu_id FROM sys_menu WHERE path IN ('rebate')
);

-- 2. 删除菜单
DELETE FROM sys_menu WHERE path IN ('rebate');
