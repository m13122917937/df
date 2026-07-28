-- 删除商品成本/成本系数菜单及角色关联
-- 菜单路径: businessAnalysis/productAccounting/fixedCoefficient
-- 执行日期: 2026-07-27

-- 1. 删除角色关联
DELETE FROM sys_role_menu WHERE menu_id IN (
    SELECT menu_id FROM sys_menu WHERE path IN ('fixedCoefficient', 'productAccounting')
);

-- 2. 删除成本系数子菜单 (path = 'fixedCoefficient')
DELETE FROM sys_menu WHERE path = 'fixedCoefficient' AND parent_id = (
    SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE path = 'productAccounting') AS tmp
);

-- 3. 删除商品成本父菜单 (path = 'productAccounting')
DELETE FROM sys_menu WHERE path = 'productAccounting';
