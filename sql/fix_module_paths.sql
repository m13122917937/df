-- ============================================================
-- 修复：四个一级模块的 path 去掉前导 "/"
-- getRouterPath() 会自动为 parent_id=0 的目录加上 "/"
-- 如果 DB 的 path 已经以 "/" 开头，结果会变成 "//" 导致路由失效
-- ============================================================

UPDATE sys_menu SET path = 'sys'         WHERE menu_name = '系统'     AND parent_id = 0 AND path = '/sys';
UPDATE sys_menu SET path = 'supplychain' WHERE menu_name = '供应链'    AND parent_id = 0 AND path = '/supplychain';
UPDATE sys_menu SET path = 'dashboard'   WHERE menu_name = '经营分析'  AND parent_id = 0 AND path = '/dashboard';
UPDATE sys_menu SET path = 'tools'       WHERE menu_name = '工具箱'    AND parent_id = 0 AND path = '/tools';

-- 验证修复结果
SELECT menu_id, menu_name, path, parent_id FROM sys_menu
WHERE parent_id = 0 AND menu_name IN ('经营分析','供应链','工具箱','系统')
ORDER BY order_num;
