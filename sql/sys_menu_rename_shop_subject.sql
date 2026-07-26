-- 标签页标题由 sys_menu.menu_name 经 /getRouters 下发。
-- 将历史菜单名“销售-店铺主体”统一为面向用户展示的“店铺主体”。
UPDATE `sys_menu`
SET `menu_name` = '店铺主体',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `menu_name` = '销售-店铺主体'
  AND `del_flag` = '0';
